package eu.pawelniewiadomski.java.hybris.patches.service.impl;

import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;

import org.springframework.beans.factory.annotation.Required;

import eu.pawelniewiadomski.java.hybris.patches.service.PatchInfoService;
import eu.pawelniewiadomski.java.hybris.patches.servicelayer.dao.PatchInfoDao;
import eu.pawelniewiadomski.java.hybris.patches.model.PatchInfoModel;



public class DefaultPatchInfoService extends AbstractBusinessService implements PatchInfoService
{
	private PatchInfoDao patchInfoDao;

	@Override
	public void createOrUpdatePatchInfo(final String fileName, final long lastModified)
	{
		PatchInfoModel patchInfo = patchInfoDao.getPatchInfoByFileName(fileName);
		if (patchInfo == null)
		{
			patchInfo = getModelService().create(PatchInfoModel.class);
			patchInfo.setPatchFileName(fileName);
		}
		patchInfo.setLastModified(Long.valueOf(lastModified));
		getModelService().save(patchInfo);
	}

	@Override
	public boolean isPatchInfoUpToDate(final String fileName, final long lastModified)
	{

		final PatchInfoModel patchInfo = patchInfoDao.getPatchInfoByFileName(fileName);
		if (patchInfo != null)
		{
			return patchInfo.getLastModified().longValue() == lastModified;
		}
		return false;
	}


	public PatchInfoDao getPatchInfoDao()
	{
		return patchInfoDao;
	}

	@Required
	public void setPatchInfoDao(final PatchInfoDao patchInfoDao)
	{
		this.patchInfoDao = patchInfoDao;
	}
}
