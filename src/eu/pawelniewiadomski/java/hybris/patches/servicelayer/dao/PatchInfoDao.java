package eu.pawelniewiadomski.java.hybris.patches.servicelayer.dao;

import eu.pawelniewiadomski.java.hybris.patches.model.PatchInfoModel;


public interface PatchInfoDao
{
	PatchInfoModel getPatchInfoByFileName(String fileName);

}
