package ca.uhn.fhir.parser;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.ExtensionDt;
import ca.uhn.fhir.model.api.ResourceMetadataKeyEnum;
import ca.uhn.fhir.model.dev.resource.MedicationPrescription;
import ca.uhn.fhir.model.dev.resource.Patient;
import ca.uhn.fhir.model.dev.resource.QuestionnaireAnswers;
import ca.uhn.fhir.model.dstu.resource.Binary;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import ca.uhn.fhir.model.primitive.StringDt;

public class JsonParserTest {
	private static final org.slf4j.Logger ourLog = org.slf4j.LoggerFactory.getLogger(JsonParserTest.class);
	private static final FhirContext ourCtx = FhirContext.forDev();

	@Test
	public void testParseBundleWithBinary() {
		Binary patient = new Binary();
		patient.setId(new IdDt("http://base/Binary/11/_history/22"));
		patient.setContentType("foo");
		patient.setContent(new byte[] { 1, 2, 3, 4 });

		String val = ourCtx.newJsonParser().encodeResourceToString(patient);
		assertEquals("{\"resourceType\":\"Binary\",\"id\":\"11\",\"meta\":{\"versionId\":\"22\"},\"contentType\":\"foo\",\"content\":\"AQIDBA==\"}", val);
	}

	/**
	 * #65
	 */
	@Test
	public void testJsonPrimitiveWithExtensionEncoding() {
		
		QuestionnaireAnswers parsed = new QuestionnaireAnswers();
		parsed.getGroup().setLinkId("value123");
		parsed.getGroup().getLinkIdElement().addUndeclaredExtension(false, "http://123", new StringDt("HELLO"));
	
		String encoded = ourCtx.newJsonParser().setPrettyPrint(false).encodeResourceToString(parsed);
		ourLog.info(encoded);
		assertThat(encoded, containsString("{\"linkId\":\"value123\",\"_linkId\":{\"http://123\":[{\"valueString\":\"HELLO\"}]}}"));
		
	}

	
	@Test
	public void testParsePatientInBundle() {
		
		String text = "{\"resourceType\":\"Bundle\",\"id\":null,\"base\":\"http://localhost:57931/fhir/contextDev\",\"total\":1,\"link\":[{\"relation\":\"self\",\"url\":\"http://localhost:57931/fhir/contextDev/Patient?identifier=urn%3AMultiFhirVersionTest%7CtestSubmitPatient01&_format=json\"}],\"entry\":[{\"resource\":{\"resourceType\":\"Patient\",\"id\":\"1\",\"meta\":{\"versionId\":\"1\",\"lastUpdated\":\"2014-12-20T18:41:29.706-05:00\"},\"identifier\":[{\"system\":\"urn:MultiFhirVersionTest\",\"value\":\"testSubmitPatient01\"}]}}]}";
		FhirContext ctx = FhirContext.forDev();
		Bundle b = ctx.newJsonParser().parseBundle(text);
		
		assertEquals(Patient.class, b.getEntries().get(0).getResource().getClass());
	}
	
	@Test
	public void testParseAndEncodeBundle() throws Exception {
		String content = IOUtils.toString(JsonParserTest.class.getResourceAsStream("/bundle-example.json"));

		Bundle parsed = ourCtx.newJsonParser().parseBundle(content);
		assertEquals("http://example.com/base/Bundle/example/_history/1", parsed.getId().getValue());
		assertEquals("1", parsed.getResourceMetadata().get(ResourceMetadataKeyEnum.VERSION));
		assertEquals("1", parsed.getId().getVersionIdPart());
		assertEquals(new InstantDt("2014-08-18T01:43:30Z"), parsed.getResourceMetadata().get(ResourceMetadataKeyEnum.UPDATED));
		assertEquals("transaction", parsed.getType().getValue());
		assertEquals(3, parsed.getTotalResults().getValue().intValue());
		assertEquals("http://example.com/base", parsed.getLinkBase().getValue());
		assertEquals("https://example.com/base/MedicationPrescription?patient=347&searchId=ff15fd40-ff71-4b48-b366-09c706bed9d0&page=2", parsed.getLinkNext().getValue());
		assertEquals("https://example.com/base/MedicationPrescription?patient=347", parsed.getLinkSelf().getValue());

		assertEquals(1, parsed.getEntries().size());
		assertEquals("update", parsed.getEntries().get(0).getStatus().getValue());

		MedicationPrescription p = (MedicationPrescription) parsed.getEntries().get(0).getResource();
		assertEquals("Patient/example", p.getPatient().getReference().getValue());
		assertEquals("2014-08-16T05:31:17Z", ResourceMetadataKeyEnum.UPDATED.get(p).getValueAsString());
		assertEquals("http://example.com/base/MedicationPrescription/3123/_history/1", p.getId().getValue());

		String reencoded = ourCtx.newJsonParser().setPrettyPrint(true).encodeBundleToString(parsed);
		ourLog.info(reencoded);

		JsonConfig cfg = new JsonConfig();

		JSON expected = JSONSerializer.toJSON(content.trim(), cfg);
		JSON actual = JSONSerializer.toJSON(reencoded.trim(), cfg);

		String exp = expected.toString().replace("\\r\\n", "\\n"); // .replace("&sect;", "§");
		String act = actual.toString().replace("\\r\\n", "\\n");

		ourLog.info("Expected: {}", exp);
		ourLog.info("Actual  : {}", act);

		assertEquals(exp, act);

	}
	
	@Test
	public void testParseAndEncodeNewExtensionFormat() {
		
		//@formatter:off
		String resource = "{\n" + 
			"  \"resourceType\" : \"Patient\",\n" + 
			"  \"http://acme.org/fhir/ExtensionDefinition/trial-status\" : [{\n" + 
			"    \"code\" : [{ \"valueCode\" : \"unsure\" }],\n" + 
			"    \"date\" : [{ \"valueDate\" : \"2009-03-14\" }], \n" + 
			"    \"registrar\" : [{ \"valueReference\" : {\n" + 
			"      \"reference\" : \"Practitioner/example\"\n" + 
			"      }\n" + 
			"    }]\n" + 
			"  }],\n" +
			"  \"modifier\" : { \n" + 
			"    \"http://example.org/fhir/ExtensionDefinition/some-kind-of-modifier\" : [{\n" + 
			"      \"valueBoolean\" : true\n" + 
			"      }]\n" + 
			"  }," +
			"  \"name\" : [{\n" + 
			"      \"family\": [\n" + 
			"        \"du\",\n" + 
			"        \"Marché\"\n" + 
			"      ],\n" + 
			"      \"_family\": [\n" + 
			"        {\n" + 
			"          \"http://hl7.org/fhir/ExtensionDefinition/iso21090-EN-qualifier\": [\n" + 
			"            {\n" + 
			"               \"valueCode\": \"VV\"\n" + 
			"            }\n" + 
			"          ]\n" + 
			"        },\n" + 
			"        null\n" + 
			"      ],\n" + 
			"      \"given\": [\n" + 
			"        \"Bénédicte\"\n" + 
			"      ]\n" + 
			"    }],\n" +
			"  \"gender\" : \"M\"\n" +
			"}";
		//@formatter:on
		
//		ourLog.info(resource);
		
		Patient parsed = ourCtx.newJsonParser().parseResource(Patient.class, resource);
		
		// Modifier extension
		assertEquals(1, parsed.getUndeclaredExtensionsByUrl("http://example.org/fhir/ExtensionDefinition/some-kind-of-modifier").size());
		assertTrue( parsed.getUndeclaredExtensionsByUrl("http://example.org/fhir/ExtensionDefinition/some-kind-of-modifier").get(0).isModifier());
		assertEquals(BooleanDt.class, parsed.getUndeclaredExtensionsByUrl("http://example.org/fhir/ExtensionDefinition/some-kind-of-modifier").get(0).getValueAsPrimitive().getClass());
		assertEquals("true", parsed.getUndeclaredExtensionsByUrl("http://example.org/fhir/ExtensionDefinition/some-kind-of-modifier").get(0).getValueAsPrimitive().getValueAsString());
		
		// Gender
		assertEquals("M",parsed.getGender());
		assertEquals(1, parsed.getUndeclaredExtensionsByUrl("http://acme.org/fhir/ExtensionDefinition/trial-status").size());
		
		// Trial status
		ExtensionDt ext = parsed.getUndeclaredExtensionsByUrl("http://acme.org/fhir/ExtensionDefinition/trial-status").get(0);
		assertNull(ext.getValue());
		assertEquals(1, ext.getUndeclaredExtensionsByUrl("http://acme.org/fhir/ExtensionDefinition/code").size());
		assertEquals(CodeDt.class, ext.getUndeclaredExtensionsByUrl("http://acme.org/fhir/ExtensionDefinition/code").get(0).getValue().getClass());
		assertEquals("unsure", ext.getUndeclaredExtensionsByUrl("http://acme.org/fhir/ExtensionDefinition/code").get(0).getValueAsPrimitive().getValueAsString());
		assertEquals(1, ext.getUndeclaredExtensionsByUrl("http://acme.org/fhir/ExtensionDefinition/date").size());
		assertEquals(DateDt.class, ext.getUndeclaredExtensionsByUrl("http://acme.org/fhir/ExtensionDefinition/date").get(0).getValue().getClass());
		assertEquals("2009-03-14", ext.getUndeclaredExtensionsByUrl("http://acme.org/fhir/ExtensionDefinition/date").get(0).getValueAsPrimitive().getValueAsString());

		// Name
		assertEquals(1, parsed.getName().size());
		assertEquals(2, parsed.getName().get(0).getFamily().size());
		assertEquals("du Marché", parsed.getName().get(0).getFamilyAsSingleString());
		assertEquals(1, parsed.getName().get(0).getGiven().size());
		assertEquals("Bénédicte", parsed.getName().get(0).getGivenAsSingleString());
		
		// Patient.name[0].family extensions
		assertEquals(1, parsed.getNameFirstRep().getFamily().get(0).getUndeclaredExtensionsByUrl("http://hl7.org/fhir/ExtensionDefinition/iso21090-EN-qualifier").size());
		assertEquals(CodeDt.class, parsed.getNameFirstRep().getFamily().get(0).getUndeclaredExtensionsByUrl("http://hl7.org/fhir/ExtensionDefinition/iso21090-EN-qualifier").get(0).getValueAsPrimitive().getClass());
		assertEquals("VV", parsed.getNameFirstRep().getFamily().get(0).getUndeclaredExtensionsByUrl("http://hl7.org/fhir/ExtensionDefinition/iso21090-EN-qualifier").get(0).getValueAsPrimitive().getValueAsString());
		
		/*
		 * Now re-encode
		 */
		String encoded = ourCtx.newJsonParser().setPrettyPrint(true).encodeResourceToString(parsed);
		ourLog.info(encoded);

		JSON expected = JSONSerializer.toJSON(resource.trim());
		JSON actual = JSONSerializer.toJSON(encoded.trim());

		String exp = expected.toString().replace("\\r\\n", "\\n"); // .replace("&sect;", "§");
		String act = actual.toString().replace("\\r\\n", "\\n");

		ourLog.info("Expected: {}", exp);
		ourLog.info("Actual  : {}", act);

		assertEquals(exp, act);

	}
	
	@Test
	public void testParseAndEncodeBundleWithDeletedEntry() {
		
		Patient res = new Patient();
		res.setId(new IdDt("Patient", "111", "222"));
		ResourceMetadataKeyEnum.DELETED_AT.put(res, new InstantDt("2011-01-01T12:12:22Z"));
		
		Bundle bundle = new Bundle();
		bundle.addResource(res, ourCtx, "http://foo/base");
		
		String encoded = ourCtx.newJsonParser().encodeBundleToString(bundle);
		ourLog.info(encoded);
		
		assertEquals("{\"resourceType\":\"Bundle\",\"id\":null,\"entry\":[{\"deleted\":{\"type\":\"Patient\",\"resourceId\":\"111\",\"versionId\":\"222\",\"instant\":\"2011-01-01T12:12:22Z\"}}]", encoded);
		
	}

}
