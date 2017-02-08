package eu.pawelniewiadomski.java.hybris.patches.impex;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Required;

import eu.pawelniewiadomski.java.hybris.patches.constants.HybrisPatchesConstants;
import eu.pawelniewiadomski.java.hybris.patches.service.PatchInfoService;


/**
 *
 *
 * This class scans for impexes to be imported in a given patch directory and its subdirectories. Impexes will be
 * imported if they match the naming convention. That is: RN_PN_IN_VIN.impex where
 *
 * <li>RN is a release number</li>
 * <li>PN is a patch number</li>
 * <li>IN is an impex number</li>
 * <li>VIN is a valid impex name</li><br/>
 * <br/>
 * Scanner skips impexes that has already been imported, unless their content was modified. Scanner keeps invalid and
 * skipped impexes lists for reporting purpose.
 */
public class PatchImpexScanner
{
	private PatchInfoService patchInfoService;

	private List<File> impexesToBeImported = null;
	private List<File> incorrectImpexes = null;
	private List<File> skippedImpexes = null;


	/**
	 *
	 * @param patchDirectoryPath
	 *           - the patch directory to be scanned
	 */
	public void findImpexesForImport(final String patchDirectoryPath)
	{
		final File patchDir = new File(patchDirectoryPath);
		final String patchRelativeDirectory = getRelativePathOfDir(patchDir.getPath());
		reset();
		scanImpexesInDir(patchDir, patchRelativeDirectory);
	}

	private void reset()
	{
		impexesToBeImported = new ArrayList<File>();
		incorrectImpexes = new ArrayList<File>();
		skippedImpexes = new ArrayList<File>();
	}

	private void scanImpexesInDir(final File patchDir, final String patchRelativeDirectory)
	{
		for (final File file : patchDir.listFiles())
		{
			if (file.isDirectory())
			{
				scanImpexesInDir(file, patchRelativeDirectory);
			}
			else
			{
				processImpex(file, patchRelativeDirectory);
			}
		}
	}


	private void processImpex(final File impexFile, final String patchRelativeDirectory)
	{
		if (isValidPatchName(impexFile.getName(), getDirectoryNameUnderscored(patchRelativeDirectory)))
		{
			if (!patchInfoService.isPatchInfoUpToDate(impexFile.getName(), impexFile.lastModified()))
			{
				impexesToBeImported.add(impexFile);
			}
			else
			{
				skippedImpexes.add(impexFile);
			}
		}
		else
		{
			incorrectImpexes.add(impexFile);
		}
	}

	private boolean isValidPatchName(final String fileName, final String directoryName)
	{
		final Pattern pattern = Pattern.compile(directoryName + "_[0-9]+_([a-zA-Z0-9]+)\\.impex");
		final Matcher matcher = pattern.matcher(fileName);
		return matcher.matches();
	}



	public List<File> getImpexesToBeImported()
	{
		return impexesToBeImported;
	}


	public List<File> getIncorrectImpexes()
	{
		return incorrectImpexes;
	}


	public List<File> getSkippedImpexes()
	{
		return skippedImpexes;
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

	static String getDirectoryNameUnderscored(final String directoriesRelPatchPath)
	{
		return directoriesRelPatchPath.replace("/", "_");
	}

	static String getRelativePathOfDir(final String directoryPath)
	{
		final String normalizedPath = FilenameUtils.separatorsToUnix(directoryPath);
		final String relativePathwithSeparator = normalizedPath.split(HybrisPatchesConstants.PATCHES_PATH)[1];
		return relativePathwithSeparator.substring(1);
	}

	public static String makePatchKey(final String directoryPath)
	{
		return getDirectoryNameUnderscored(getRelativePathOfDir(directoryPath));
	}



}
