package test.resource;

import org.osgi.resource.Requirement;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.CapabilityBuilder;
import junit.framework.TestCase;

public class CapReqBuilderTest extends TestCase {

	public void testSimple() throws Exception {
		CapabilityBuilder cb = new CapabilityBuilder("test");
	}

	public void testParseRequirement() throws Exception {
		Parameters params = OSGiHeader.parseHeader("osgi.identity; filter:='(a=b)'; resolution:=optional");
		Requirement req = CapReqBuilder.getRequirementsFrom(params).get(0);
		assertEquals("osgi.identity", req.getNamespace());
		assertEquals("optional", req.getDirectives().get("resolution"));
		assertEquals("(a=b)", req.getDirectives().get("filter"));
	}

	public void testAliasedRequirement() throws Exception {
		Parameters params = OSGiHeader.parseHeader("bundle; bsn=org.example.foo");
		Requirement req = CapReqBuilder.getRequirementsFrom(params).get(0);
		assertEquals("osgi.identity", req.getNamespace());
		assertEquals("(osgi.identity=org.example.foo)", req.getDirectives().get("filter"));
	}

	public void testAliasedRequirementWithVersion() throws Exception {
		Parameters params = OSGiHeader.parseHeader("bundle; bsn=org.example.foo; version=1.2");
		Requirement req = CapReqBuilder.getRequirementsFrom(params).get(0);
		assertEquals("osgi.identity", req.getNamespace());
		assertEquals("(&(osgi.identity=org.example.foo)(version>=1.2.0))",
				req.getDirectives().get("filter"));
	}

	public void testAliasedRequirementWithVersionRange() throws Exception {
		Parameters params = OSGiHeader.parseHeader("bundle; bsn=org.example.foo; version='[1.2,1.3)'");
		Requirement req = CapReqBuilder.getRequirementsFrom(params).get(0);

		assertEquals("osgi.identity", req.getNamespace());
		assertEquals("(&(osgi.identity=org.example.foo)(&(version>=1.2.0)(!(version>=1.3.0))))",
				req.getDirectives().get("filter"));
	}

	public void testAliasedRequirementCopyAttributesAndDirectives() throws Exception {
		Attrs attrs = new Attrs();
		attrs.putTyped("bsn", "org.example.foo");
		attrs.putTyped("size", 23L);
		attrs.put("resolution:", "optional");
		Requirement req = CapReqBuilder.getRequirementFrom("bundle", attrs);

		assertEquals("osgi.identity", req.getNamespace());
		assertEquals("(osgi.identity=org.example.foo)", req.getDirectives().get("filter"));
		assertEquals(23L, req.getAttributes().get("size"));
		assertEquals("optional", req.getDirectives().get("resolution"));
	}

}
