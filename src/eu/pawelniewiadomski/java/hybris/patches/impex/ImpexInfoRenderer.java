package eu.pawelniewiadomski.java.hybris.patches.impex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import eu.pawelniewiadomski.java.hybris.patches.data.PatchImportData;
import eu.pawelniewiadomski.java.hybris.patches.impex.ImpexInfoRendererContext.ImpexesStatus;
import eu.pawelniewiadomski.java.hybris.patches.impex.ImpexInfoRendererContext.RenderingScope;


public class ImpexInfoRenderer
{
	public static final String IMPORTEDIMPEX_ROW_BEGIN = "<tr><th class=\"green\" style=\"font-weight: bold;\">";
	public static final String INCORRECTIMPEX_ROW_BEGIN = "<tr><th class=\"red\">";
	public static final String COMMENTS_ROW_BEGIN = "<tr><th class=\"gray\">";
	public static final String SKIPPEDIMPEX_ROW_BEGIN = "<tr><th>";
	public static final String DEFAULT_ROW_END = "</th></tr>\n";


	private ImpexInfoRendererContext rendererContext;

	public String renderPatchImportProgressForHac(final PatchImportData patchImportData)
	{
		rendererContext = new ImpexInfoRendererContext(RenderingScope.HEADERS_ONLY);
		if (patchImportData != null)
		{
			renderImpexCollection(rendererContext, patchImportData.importedImpexes, ImpexesStatus.IMPORTED);
			renderImpexCollection(rendererContext, patchImportData.skippedImpexes, ImpexesStatus.SKIPPED);
			renderImpexCollection(rendererContext, patchImportData.incorrectImpexes, ImpexesStatus.INCORRECT);
		}
		return rendererContext.getMessageBuilder().toString();
	}

	public String renderPatchImportStatusForEmail(final PatchImportData patchImportData)
	{
		rendererContext = new ImpexInfoRendererContext(RenderingScope.FULL_DETAILS);
		if (patchImportData != null)
		{
			renderImpexCollection(rendererContext, patchImportData.importedImpexes, ImpexesStatus.IMPORTED);
			renderImpexCollection(rendererContext, patchImportData.skippedImpexes, ImpexesStatus.SKIPPED);
			renderImpexCollection(rendererContext, patchImportData.incorrectImpexes, ImpexesStatus.INCORRECT);
		}
		return rendererContext.getMessageBuilder().toString();
	}

	private void renderImpexCollection(final ImpexInfoRendererContext rendererContext, final Collection<File> impexes,
			final ImpexesStatus status)
	{
		rendererContext.impexesStatus = status;
		if (impexes != null)
		{
			for (final File impexFile : impexes)
			{
				renderImpexBasicInfo(rendererContext, impexFile);
				renderImpexDetails(rendererContext, impexFile);
			}
		}
	}

	private void renderImpexBasicInfo(final ImpexInfoRendererContext rendererContext, final File impexFile)
	{
		final StringBuilder msg = rendererContext.getMessageBuilder();
		switch (rendererContext.impexesStatus)
		{
			case IMPORTED:
				msg.append(IMPORTEDIMPEX_ROW_BEGIN);
				break;
			case INCORRECT:
				msg.append(INCORRECTIMPEX_ROW_BEGIN);
				break;
			case SKIPPED:
				msg.append(IMPORTEDIMPEX_ROW_BEGIN);
				break;
		}
		msg.append(impexFile.getName());
		msg.append(rendererContext.impexesStatus.getStatusSuffix());
		msg.append(DEFAULT_ROW_END);
		if (rendererContext.renderingScope == RenderingScope.HEADERS_ONLY)
		{
			msg.append("<br/>");
		}
	}

	private void renderImpexDetails(final ImpexInfoRendererContext rendererContext, final File impexFile)
	{
		final StringBuilder msg = rendererContext.getMessageBuilder();
		if (rendererContext.renderingScope == RenderingScope.FULL_DETAILS)
		{
			final String comments = getCommentsFromImpexFile(impexFile);
			if (comments.length() > 1)
			{
				msg.append(COMMENTS_ROW_BEGIN);
				msg.append(comments);
				msg.append(DEFAULT_ROW_END);
			}
		}
	}

	private String getCommentsFromImpexFile(final File impexFile)
	{
		final StringBuilder comments = new StringBuilder();
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(impexFile)));
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.startsWith("#"))
				{
					comments.append(line + "</br>");
				}
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				br.close();
			}
			catch (final IOException e)
			{
				e.printStackTrace();
			}
		}
		return comments.toString();
	}
}
