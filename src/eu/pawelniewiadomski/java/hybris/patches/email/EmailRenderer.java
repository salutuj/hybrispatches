package eu.pawelniewiadomski.java.hybris.patches.email;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Required;

import eu.pawelniewiadomski.java.hybris.patches.data.PatchImportData;
import eu.pawelniewiadomski.java.hybris.patches.impex.ImpexInfoRenderer;


public class EmailRenderer
{
	public static final String PATCH_TABLE_BEGIN = "<style> table {border-collapse:collapse;} table,td,th {border:1px solid black;} th.gray{color:#B3B3B3;text-align: left;} th.red{color:#D10F0F;} th.green{color:#31782E}</style><table>\n";
	public static final String PATCH_TABLE_END = "</table>\n";
	public static final String PATCH_HEADER_BEGIN = "<h2>Patch: ";
	public static final String PATCH_HEADER_END = "</h2>\n";
	private ImpexInfoRenderer impexInfoRenderer;

	public String renderSubjectWithPatchesImportData(final String subjectTemplate,
			final Map<String, PatchImportData> patchesImportData)
	{
		final String renderedSubject = subjectTemplate;
		return renderedSubject;
	}

	public String renderBodyWithPatchesImportData(final Map<String, PatchImportData> patchesImportData)
	{
		final StringBuilder bodyBuilder = new StringBuilder(8192);
		if (patchesImportData != null)
		{
			for (final Entry<String, PatchImportData> patchImportDataEntry : patchesImportData.entrySet())
			{
				bodyBuilder.append(PATCH_HEADER_BEGIN);
				bodyBuilder.append(patchImportDataEntry.getKey());
				bodyBuilder.append(PATCH_HEADER_END);
				bodyBuilder.append(PATCH_TABLE_BEGIN);
				final String patchTable = impexInfoRenderer.renderPatchImportStatusForEmail(patchImportDataEntry.getValue());
				bodyBuilder.append(patchTable);
				bodyBuilder.append(PATCH_TABLE_END);
			}
		}
		if (bodyBuilder.length() > 0)
		{
			return bodyBuilder.toString();
		}
		else
		{
			return generateBodyForEmptyPatchesMap();
		}
	}

	/**
	 * @return
	 */
	private String generateBodyForEmptyPatchesMap()
	{
		//TODO: turn into i18n
		return "<h1>Update was executed but no patches were imported</h1>";
	}

	/**
	 * @return the impexInfoRenderer
	 */
	public ImpexInfoRenderer getImpexInfoRenderer()
	{
		return impexInfoRenderer;
	}

	/**
	 * @param impexInfoRenderer
	 *           the impexInfoRenderer to set
	 */
	@Required
	public void setImpexInfoRenderer(final ImpexInfoRenderer impexInfoRenderer)
	{
		this.impexInfoRenderer = impexInfoRenderer;
	}
}
