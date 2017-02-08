package eu.pawelniewiadomski.java.hybris.patches.data;

import java.io.File;
import java.util.Collection;



public class PatchImportData
{

	public final Collection<File> importedImpexes;
	public final Collection<File> incorrectImpexes;
	public final Collection<File> skippedImpexes;

	public PatchImportData(final Collection<File> importedImpexes, final Collection<File> skippedImpexes,
			final Collection<File> incorrectImpexes)
	{
		this.importedImpexes = importedImpexes;
		this.skippedImpexes = skippedImpexes;
		this.incorrectImpexes = incorrectImpexes;
	}
}
