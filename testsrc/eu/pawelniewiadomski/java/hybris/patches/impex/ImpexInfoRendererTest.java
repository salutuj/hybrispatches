package eu.pawelniewiadomski.java.hybris.patches.impex;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import eu.pawelniewiadomski.java.hybris.patches.data.PatchImportData;



@UnitTest
public class ImpexInfoRendererTest
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ImpexInfoRendererTest.class.getName());

	@InjectMocks
	private ImpexInfoRenderer impexInfoRenderer;

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
	public void testRenderPatchImportProgressForHacWithNull()
	{
		final String renderedContent = impexInfoRenderer.renderPatchImportProgressForHac(null);
		assertTrue(renderedContent.isEmpty());
	}

	@Test
	public void testRenderPatchImportStatusForEmailWithNull()
	{
		final String renderedContent = impexInfoRenderer.renderPatchImportStatusForEmail(null);
		assertTrue(renderedContent.isEmpty());
	}

	@Test
	public void testRenderPatchImportProgressForHacWithNullImpexLists()
	{
		final PatchImportData patchImportData = new PatchImportData(null, null, null);
		final String renderedContent = impexInfoRenderer.renderPatchImportProgressForHac(patchImportData);
		assertTrue(renderedContent.isEmpty());
	}

	@Test
	public void testRenderPatchImportStatusForEmailWithNullImpexLists()
	{
		final PatchImportData patchImportData = new PatchImportData(null, null, null);
		final String renderedContent = impexInfoRenderer.renderPatchImportStatusForEmail(patchImportData);
		assertTrue(renderedContent.isEmpty());
	}

	@Test
	public void testRenderPatchImportProgressForHacWithOneEmptyImpexList()
	{
		final Collection<File> importedImpexes = new ArrayList<>();
		final PatchImportData patchImportData = new PatchImportData(importedImpexes, null, null);
		final String renderedContent = impexInfoRenderer.renderPatchImportProgressForHac(patchImportData);
		assertTrue(renderedContent.isEmpty());
	}

	@Test
	public void testRenderPatchImportStatusForEmailWithOneEmptyImpexList()
	{
		final Collection<File> importedImpexes = new ArrayList<>();
		final PatchImportData patchImportData = new PatchImportData(importedImpexes, null, null);
		final String renderedContent = impexInfoRenderer.renderPatchImportStatusForEmail(patchImportData);
		assertTrue(renderedContent.isEmpty());
	}

	@Test
	public void testRenderPatchImportProgressForHacWithAllEmptyImpexLists()
	{
		final Collection<File> importedImpexes = new ArrayList<>();
		final Collection<File> skippedImpexes = new ArrayList<>();
		final Collection<File> incorrectImpexes = new ArrayList<>();
		final PatchImportData patchImportData = new PatchImportData(importedImpexes, skippedImpexes, incorrectImpexes);
		final String renderedContent = impexInfoRenderer.renderPatchImportProgressForHac(patchImportData);
		assertTrue(renderedContent.isEmpty());
	}

	@Test
	public void testRenderPatchImportStatusForEmailWithAllEmptyImpexLists()
	{
		final Collection<File> importedImpexes = new ArrayList<>();
		final Collection<File> skippedImpexes = new ArrayList<>();
		final Collection<File> incorrectImpexes = new ArrayList<>();
		final PatchImportData patchImportData = new PatchImportData(importedImpexes, skippedImpexes, incorrectImpexes);
		final String renderedContent = impexInfoRenderer.renderPatchImportStatusForEmail(patchImportData);
		assertTrue(renderedContent.isEmpty());
	}
}
