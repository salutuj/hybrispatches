package eu.pawelniewiadomski.java.hybris.patches.impex;

import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import eu.pawelniewiadomski.java.hybris.patches.service.PatchInfoService;


@UnitTest
public class PatchImpexScannerTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(PatchImpexScannerTest.class.getName());

	@Mock
	private PatchInfoService patchInfoService;

	@InjectMocks
	PatchImpexScanner patchImpexScanner = new PatchImpexScanner();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown()
	{
		// implement here code executed after each test
	}

	@Test
	public void testFindImpexesForImport()
	{
		when(Boolean.valueOf(patchInfoService.isPatchInfoUpToDate(Mockito.anyString(), Mockito.anyLong())))
				.thenReturn(Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
		// TODO: make impex scanner be able to parse testpatches folder
		patchImpexScanner.findImpexesForImport("/hybrispatches/resources/hybrispatches/import/testpatches/r1/p_1.0");

	}




}
