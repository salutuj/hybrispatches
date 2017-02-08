package eu.pawelniewiadomski.java.hybris.patches.servicelayer.dao.impl;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import com.sun.istack.internal.logging.Logger;

import eu.pawelniewiadomski.java.hybris.patches.model.PatchInfoModel;
import eu.pawelniewiadomski.java.hybris.patches.servicelayer.dao.PatchInfoDao;


public class PatchInfoDaoImpl extends AbstractItemDao implements PatchInfoDao
{
	private static Logger LOG = Logger.getLogger(PatchInfoDaoImpl.class);

	@Override
	public PatchInfoModel getPatchInfoByFileName(final String fileName)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pi." + PatchInfoModel.PK + "} FROM {"
				+ PatchInfoModel._TYPECODE + " AS pi} WHERE {pi." + PatchInfoModel.PATCHFILENAME + "} = ?fileName");
		query.addQueryParameter("fileName", fileName);
		PatchInfoModel patchInfoModel;
		try
		{
			patchInfoModel = (PatchInfoModel) getFlexibleSearchService().searchUnique(query);
			return patchInfoModel;
		}
		catch (final ModelNotFoundException e)
		{
			LOG.warning("PatchInfo not found: " + fileName);
		}
		return null;
	}

}
