package eu.pawelniewiadomski.java.hybris.patches.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import eu.pawelniewiadomski.java.hybris.patches.data.PatchImportData;
import eu.pawelniewiadomski.java.hybris.patches.impex.ImpexInfoRenderer;



@UnitTest
public class EmailRendererTest
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(EmailRendererTest.class.getName());

	@Mock
	ImpexInfoRenderer impexInfoRenderer;

	@InjectMocks
	EmailRenderer emailRenderer = new EmailRenderer();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown()
	{
		// NOP
	}


	@Test
	public void testBodyRenderingWithNullPatchesImportData()
	{
		final String renderedContent = emailRenderer.renderBodyWithPatchesImportData(null);
		assertNotEquals(0, renderedContent.length());
	}

	@Test
	public void testBodyRenderingWithEmptyPatchesImportData()
	{
		final Map<String, PatchImportData> patchesImportData = new HashMap<String, PatchImportData>();
		final String renderedContent = emailRenderer.renderBodyWithPatchesImportData(patchesImportData);
		assertNotEquals(0, renderedContent.length());
	}

	@Test
	public void testBodyRenderingWithEmptyPatch()
	{
		final String expectedResponse = "<h2>Patch: ThePatch</h2>\n"
				+ "<style> table {border-collapse:collapse;} table,td,th {border:1px solid black;} "
				+ "th.gray{color:#B3B3B3;text-align: left;} th.red{color:#D10F0F;} th.green{color:#31782E}</style>"
				+ "<table>\n</table>\n";
		final Map<String, PatchImportData> patchesImportData = new HashMap<String, PatchImportData>();
		patchesImportData.put("ThePatch", null);
		Mockito.when(impexInfoRenderer.renderPatchImportStatusForEmail(Mockito.any(PatchImportData.class))).thenReturn("");
		final String renderedContent = emailRenderer.renderBodyWithPatchesImportData(patchesImportData);
		assertEquals(expectedResponse, renderedContent);
	}


}
