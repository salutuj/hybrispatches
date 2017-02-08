/*
* Copyright (c) Pawel Niewiadomski 2017
* All Rights Reserved.
*/
package eu.pawelniewiadomski.java.hybris.patches.service;


public interface PatchInfoService
{
	public void createOrUpdatePatchInfo(final String fileName, final long lastModified);

	public boolean isPatchInfoUpToDate(final String fileName, final long lastModified);
}
