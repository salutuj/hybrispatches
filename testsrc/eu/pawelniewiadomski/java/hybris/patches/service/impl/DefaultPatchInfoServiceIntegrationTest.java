package eu.pawelniewiadomski.java.hybris.patches.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.pawelniewiadomski.java.hybris.patches.model.PatchInfoModel;
import eu.pawelniewiadomski.java.hybris.patches.servicelayer.dao.PatchInfoDao;



@IntegrationTest
public class DefaultPatchInfoServiceIntegrationTest extends ServicelayerTransactionalTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultPatchInfoServiceIntegrationTest.class.getName());

	@Resource
	ModelService modelService;

	@Resource
	PatchInfoDao patchInfoDao;

	DefaultPatchInfoService defaultPatchInfoService;



	@Before
	public void setUp()
	{
		defaultPatchInfoService = new DefaultPatchInfoService();
		defaultPatchInfoService.setPatchInfoDao(patchInfoDao);
		defaultPatchInfoService.setModelService(modelService);
	}

	@Test
	public void testPatchInfoIsCreated()
	{
		final String fileName = "testPatchInfoIsCreated.impex";
		assertNull(getPatchInfoDao().getPatchInfoByFileName(fileName));
		defaultPatchInfoService.createOrUpdatePatchInfo(fileName, System.currentTimeMillis());
		assertNotNull(getPatchInfoDao().getPatchInfoByFileName(fileName));
	}

	@Test
	public void testPatchInfoIsUpdated()
	{
		final String fileName = "testPatchInfoIsUpdated.impex";
		defaultPatchInfoService.createOrUpdatePatchInfo(fileName, System.currentTimeMillis());
		PatchInfoModel patchInfoModel = getPatchInfoDao().getPatchInfoByFileName(fileName);
		assertNotNull(patchInfoModel);
		final long lastModified = System.currentTimeMillis();
		assertNotEquals(lastModified, patchInfoModel.getLastModified().longValue());
		defaultPatchInfoService.createOrUpdatePatchInfo(fileName, lastModified);
		patchInfoModel = getPatchInfoDao().getPatchInfoByFileName(fileName);
		assertEquals(lastModified, patchInfoModel.getLastModified().longValue());
	}

	@Test
	public void testPatchIsUptoDate()
	{
		final long lastModified = System.currentTimeMillis();
		defaultPatchInfoService.createOrUpdatePatchInfo("isUpToDate.impex", lastModified);
		sleep(5);
		final boolean isUpToDate = defaultPatchInfoService.isPatchInfoUpToDate("isUpToDate.impex", lastModified);
		assertTrue(isUpToDate);
	}

	@Test
	public void testPatchIsNotUpToDate()
	{
		defaultPatchInfoService.createOrUpdatePatchInfo("isNotUpToDate.impex", System.currentTimeMillis());
		sleep(5);
		final boolean isUpToDate = defaultPatchInfoService.isPatchInfoUpToDate("isNotUpToDate.impex", System.currentTimeMillis());
		assertFalse(isUpToDate);
	}

	private PatchInfoDao getPatchInfoDao()
	{
		return patchInfoDao;
	}

	private void sleep(final long i)
	{
		try
		{
			Thread.sleep(i);
		}
		catch (final InterruptedException e)
		{
			// ignore
		}
	}

}
