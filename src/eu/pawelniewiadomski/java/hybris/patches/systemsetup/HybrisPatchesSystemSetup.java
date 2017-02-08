package eu.pawelniewiadomski.java.hybris.patches.systemsetup;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import eu.pawelniewiadomski.java.hybris.patches.constants.HybrisPatchesConstants;
import eu.pawelniewiadomski.java.hybris.patches.data.PatchImportData;
import eu.pawelniewiadomski.java.hybris.patches.event.PatchesImportedEvent;
import eu.pawelniewiadomski.java.hybris.patches.impex.ImpexInfoRenderer;
import eu.pawelniewiadomski.java.hybris.patches.impex.PatchImpexScanner;
import eu.pawelniewiadomski.java.hybris.patches.service.PatchInfoService;





@SystemSetup(extension = HybrisPatchesConstants.EXTENSIONNAME)
public class HybrisPatchesSystemSetup extends AbstractSystemSetup
{
	private static Logger LOG = Logger.getLogger(HybrisPatchesSystemSetup.class);


	private final String OPTION_ALLPATCHES_LABEL = "All Patches";
	private final String OPTION_INDEXSOLR_LABEL = "Indexing Solr";
	private final String OPTION_SYNCCATALOGS_LABEL = "Synchronize Catalogs";
	private final String OPTION_SENDRELEASENOTES_LABEL = "Send release notes via email ";

	private ConfigurationService configurationService;
	private PatchInfoService patchInfoService;
	private SystemSetupContext setupContext;

	private PatchImpexScanner patchImpexScanner;
	private ImpexInfoRenderer impexInfoRenderer;
	private boolean executeAllPatches;
	private boolean executeIndexingSolr;
	private boolean executeSynchronizeCatalogs;
	private boolean sendEmailMessage;

	private final Map<String, PatchImportData> processedPatches = new HashMap<String, PatchImportData>();




	@Override
	@SystemSetupParameterMethod
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();
		final Map<String, String> directoriesWithDescriptions = loadDirectoriesMap(true);
		params.add(createBooleanSystemSetupParameter(OPTION_ALLPATCHES_LABEL, OPTION_ALLPATCHES_LABEL, false));
		params.add(createBooleanSystemSetupParameter(OPTION_INDEXSOLR_LABEL, OPTION_INDEXSOLR_LABEL, false));
		params.add(createBooleanSystemSetupParameter(OPTION_SYNCCATALOGS_LABEL, OPTION_SYNCCATALOGS_LABEL, false));
		params.add(createBooleanSystemSetupParameter(OPTION_SENDRELEASENOTES_LABEL, OPTION_SENDRELEASENOTES_LABEL, false));
		for (final String directoryName : directoriesWithDescriptions.keySet())
		{
			params.add(createBooleanSystemSetupParameter(directoryName, directoriesWithDescriptions.get(directoryName), false));
		}
		return params;
	}

	/*
	 * commandline init
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.INIT)
	public void createProjectDataInit(final SystemSetupContext context)
	{
		setupContext = context;
		//initUpdateProjectData(context);
	}

	/*
	 * commandline update
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.UPDATE)
	public void createProjectDataUpdate(final SystemSetupContext context)
	{
		setupContext = context;
		//initUpdateProjectData(context);
	}


	private void initUpdateProjectData(final SystemSetupContext context)
	{
		setupContext = context;
		final Configuration cfg = configurationService.getConfiguration();
		executeAllPatches = true;
		executeIndexingSolr = cfg.getBoolean(HybrisPatchesConstants.CFG_INDEXSOLR, false);
		executeSynchronizeCatalogs = cfg.getBoolean(HybrisPatchesConstants.CFG_SYNCCATALOGS, false);
		sendEmailMessage = true;
		importIndexAndSync();
	}

	/*
	 * this method is executed from hac
	 */
	@SystemSetup(type = Type.PROJECT, process = Process.ALL)
	public void createProjectDataAll(final SystemSetupContext context)
	{
		setupContext = context;
		executeAllPatches = getBooleanSystemSetupParameter(context, OPTION_ALLPATCHES_LABEL);
		executeIndexingSolr = getBooleanSystemSetupParameter(context, OPTION_INDEXSOLR_LABEL);
		executeSynchronizeCatalogs = getBooleanSystemSetupParameter(context, OPTION_SYNCCATALOGS_LABEL);
		sendEmailMessage = getBooleanSystemSetupParameter(context, OPTION_SENDRELEASENOTES_LABEL);
		importIndexAndSync();
	}


	/*
	 * Creates a map of patch directries in a format of relasase_patch like r1_p1. Depending on the parameter
	 * withDescriptions the map will contain full paths or short descriptions nicely rendered in hac.
	 *
	 * TODO: change this to make the method not being executed unnecessarily twice
	 */
	private Map<String, String> loadDirectoriesMap(final boolean withDescriptions)
	{
		final Map<String, String> returnDirectoriesMap = new TreeMap<String, String>();
		final File patchesDirectory = getResourceAsFile(HybrisPatchesConstants.PATCHES_PATH);
		if (patchesDirectory.exists())
		{
			for (final File releaseDirectory : patchesDirectory.listFiles())
			{
				if (releaseDirectory.isDirectory())
				{
					for (final File patchDirectory : releaseDirectory.listFiles())
					{
						if (patchDirectory.isDirectory())
						{
							final String directoryMapValue = withDescriptions ? createHacDescription(releaseDirectory, patchDirectory)
									: patchDirectory.getAbsolutePath();
							returnDirectoriesMap.put(createDirectoryMapKey(releaseDirectory, patchDirectory), directoryMapValue);
						}
					}
				}
			}
		}
		return returnDirectoriesMap;
	}

	private File getResourceAsFile(final String patchesPath)
	{
		return new File(getClass().getResource(patchesPath).getPath());
	}

	private String createHacDescription(final File releaseDirectory, final File patchDirectory)
	{
		final String releaseDirectoryDescription = "Release " + releaseDirectory.getName().split("r")[1];
		final String patchDirectoryName = patchDirectory.getName();
		String patchDirectoryDescription = "";
		if (patchDirectoryName.startsWith("p"))
		{
			patchDirectoryDescription = " - patch" + patchDirectoryName.split("p")[1];
		}
		else if (patchDirectoryName.startsWith("test"))
		{
			patchDirectoryDescription = "- " + patchDirectoryName;
		}
		return releaseDirectoryDescription + patchDirectoryDescription;
	}

	private String createDirectoryMapKey(final File releaseDirectory, final File patchDirectory)
	{
		return releaseDirectory.getName() + "_" + patchDirectory.getName();
	}

	private void importIndexAndSync()
	{
		importPatchesKeepAndLogThemToHac();
		if (executeIndexingSolr)
		{
			startSolrIndexing();
		}
		if (executeSynchronizeCatalogs)
		{
			startSynchronizeCatalog();
		}
		if (sendEmailMessage)
		{
			logInfo(setupContext, "Patches import finished. Sending e-mail notification");
			final PatchesImportedEvent patchesImportedEvent = new PatchesImportedEvent();
			patchesImportedEvent.setPatchesImportData(processedPatches);
			getEventService().publishEvent(patchesImportedEvent);
		}
	}

	private void importPatchesKeepAndLogThemToHac()
	{
		final Map<String, String> directoriesPathsMap = loadDirectoriesMap(false);

		for (final String patchDirectory : directoriesPathsMap.values())
		{
			final String patchKey = PatchImpexScanner.makePatchKey(patchDirectory);
			logInfo(setupContext, "Checking patch: " + patchKey);
			if (executeAllPatches || shouldBeImported(patchDirectory))
			{
				final PatchImportData patchImportData = importPatch(patchDirectory);
				processedPatches.put(patchKey, patchImportData);
				logInfo(setupContext, impexInfoRenderer.renderPatchImportProgressForHac(patchImportData));
			}
		}
	}

	private boolean shouldBeImported(final String patchDirectory)
	{
		final String patchDirectorySetupParam = PatchImpexScanner.makePatchKey(patchDirectory);
		return getBooleanSystemSetupParameter(setupContext, patchDirectorySetupParam);
	}

	private PatchImportData importPatch(final String patchDirectory)
	{
		final ArrayList<File> importedImpexes = new ArrayList<File>();
		getPatchImpexScanner().findImpexesForImport(patchDirectory);
		final PatchImpexScanner patchImpexScanner = getPatchImpexScanner();
		for (final File impexFile : patchImpexScanner.getImpexesToBeImported())
		{
			final String impexRelativePath = getImpexRelativePath(impexFile);
			importImpexFile(setupContext, impexRelativePath, true);
			getPatchInfoService().createOrUpdatePatchInfo(impexFile.getName(), impexFile.lastModified());
			importedImpexes.add(impexFile);
		}
		return new PatchImportData(importedImpexes, patchImpexScanner.getSkippedImpexes(), patchImpexScanner.getIncorrectImpexes());
	}

	private String getImpexRelativePath(final File impexFile)
	{
		final String normalizedPath = FilenameUtils.separatorsToUnix(impexFile.getPath());
		//we cannot use simply the name, as impex file can be in subfolders, this is a workaround
		final String impexNameWithPatchDirs = normalizedPath.split(HybrisPatchesConstants.PATCHES_PATH)[1];
		return HybrisPatchesConstants.PATCHES_PATH + impexNameWithPatchDirs;
	}

	private void startSolrIndexing()
	{
		final String solrConfigurationFromProperties = configurationService.getConfiguration()
				.getString(HybrisPatchesConstants.CFG_SOLRCONFIG);
		final String[] configNames = solrConfigurationFromProperties.split(" ");
		logInfo(setupContext, "Solr indexing - start");
		for (final String config : configNames)
		{
			getSetupSolrIndexerService().executeSolrIndexerCronJob(config.trim(), true);
		}
		logInfo(setupContext, "Solr indexing - done");

	}

	private void startSynchronizeCatalog()
	{
		final String catalogNamesProperty = configurationService.getConfiguration()
				.getString(HybrisPatchesConstants.CFG_CATALOGNAME);
		final String[] catalogNames = catalogNamesProperty.split(" ");
		logInfo(setupContext, "Catalog sync - start");
		for (final String catalog : catalogNames)
		{
			final PerformResult pr = getSetupSyncJobService().executeCatalogSyncJob(catalog.trim());
			if (pr.getResult().equals(CronJobResult.UNKNOWN))
			{
				logError(setupContext, "Couldn't find 'SyncItemJob' for catalog " + catalog, null);
			}
			else
			{
				logInfo(setupContext, "Synchronize catalog " + catalog + " - " + pr.getResult());
			}
		}
	}


	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


	public PatchInfoService getPatchInfoService()
	{
		return patchInfoService;
	}

	@Required
	public void setPatchInfoService(final PatchInfoService patchInfoService)
	{
		this.patchInfoService = patchInfoService;
	}

	public PatchImpexScanner getPatchImpexScanner()
	{
		return patchImpexScanner;
	}

	@Required
	public void setPatchImpexScanner(final PatchImpexScanner patchImpexScanner)
	{
		this.patchImpexScanner = patchImpexScanner;
	}

	public ImpexInfoRenderer getImpexInfoRenderer()
	{
		return impexInfoRenderer;
	}

	@Required
	public void setImpexInfoRenderer(final ImpexInfoRenderer impexInfoRenderer)
	{
		this.impexInfoRenderer = impexInfoRenderer;
	}
}
