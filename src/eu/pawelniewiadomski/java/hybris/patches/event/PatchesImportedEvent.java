package eu.pawelniewiadomski.java.hybris.patches.event;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

import java.util.Map;

import eu.pawelniewiadomski.java.hybris.patches.data.PatchImportData;


public class PatchesImportedEvent extends AbstractEvent
{
	private Map<String, PatchImportData> patchesImportData;

	public Map<String, PatchImportData> getPatchesImportData()
	{
		return patchesImportData;
	}

	public void setPatchesImportData(final Map<String, PatchImportData> patchesImportData)
	{
		this.patchesImportData = patchesImportData;
	}


}
