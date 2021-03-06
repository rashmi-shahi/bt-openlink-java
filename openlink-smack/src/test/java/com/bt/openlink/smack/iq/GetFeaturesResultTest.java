package com.bt.openlink.smack.iq;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.bt.openlink.CoreFixtures;
import com.bt.openlink.GetFeaturesFixtures;
import com.bt.openlink.OpenlinkXmppNamespace;
import com.bt.openlink.smack.Fixtures;
import com.bt.openlink.type.Feature;
import com.bt.openlink.type.FeatureId;
import com.bt.openlink.type.FeatureType;

public class GetFeaturesResultTest {
	@Rule public final ExpectedException expectedException = ExpectedException.none();

	 @BeforeClass
	    public static void setUpClass() throws Exception {
	        ProviderManager.addIQProvider("command", OpenlinkXmppNamespace.XMPP_COMMANDS.uri(), new OpenlinkIQProvider());
	    }

	    @AfterClass
	    public static void tearDownClass() throws Exception {
	        ProviderManager.removeIQProvider("command", OpenlinkXmppNamespace.XMPP_COMMANDS.uri());
	    }
    
    @Test
    public void canCreateAStanza() throws Exception {

        final GetFeaturesResult result = GetFeaturesResult.Builder.start()
                .setId(CoreFixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setProfileId(CoreFixtures.PROFILE_ID)
                .build();

        assertThat(result.getType(), is(IQ.Type.result));
        assertThat(result.getStanzaId(), is(CoreFixtures.STANZA_ID));
        assertThat(result.getTo(), is(Fixtures.TO_JID));
        assertThat(result.getFrom(), is(Fixtures.FROM_JID));
        assertThat(result.getProfileId().get(), is(CoreFixtures.PROFILE_ID));
    }

    @Test
    public void cannotCreateAStanzaWithoutAToField() throws Exception {

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The stanza 'to' has not been set");
        GetFeaturesResult.Builder.start()
                .build();
    }

    @Test
    public void willGenerateAnXmppStanza() throws Exception {

        final Feature hs1Feature = Feature.Builder.start()
                .setId(FeatureId.from("hs_1").get())
                .setType(FeatureType.HANDSET)
                .setLabel("Handset 1")
                .build();
        final Feature hs2Feature = Feature.Builder.start()
                .setId(FeatureId.from("hs_2").get())
                .setType(FeatureType.HANDSET)
                .setLabel("Handset 2")
                .build();
        final Feature privFeature = Feature.Builder.start()
                .setId(FeatureId.from("priv_1").get())
                .setType(FeatureType.PRIVACY)
                .setLabel("Privacy")
                .build();
        final Feature fwdFeature = Feature.Builder.start()
                .setId(FeatureId.from("fwd_1").get())
                .setType(FeatureType.CALL_FORWARD)
                .setLabel("Call Forward")
                .build();
        final GetFeaturesResult result = GetFeaturesResult.Builder.start()
                .setId(CoreFixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setProfileId(CoreFixtures.PROFILE_ID)
                .addFeature(hs1Feature)
                .addFeature(hs2Feature)
                .addFeature(privFeature)
                .addFeature(fwdFeature)
                .build();

        assertThat(result.toXML().toString(), isIdenticalTo(GetFeaturesFixtures.GET_FEATURES_RESULT).ignoreWhitespace());
    }

    @Test
    public void willNotBuildAPacketWithDuplicateFeatureIds() throws Exception {

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Each feature id must be unique - hs_1 appears more than once");
        final Feature hs1Feature = Feature.Builder.start()
                .setId(FeatureId.from("hs_1").get())
                .setType(FeatureType.HANDSET)
                .setLabel("Handset 1")
                .build();
        GetFeaturesResult.Builder.start()
                .setId(CoreFixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setProfileId(CoreFixtures.PROFILE_ID)
                .addFeature(hs1Feature)
                .addFeature(hs1Feature)
                .build();
    }

    @Test
    public void willNotBuildAPacketWithoutAProfileId() throws Exception {

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The get-features result profile has not been set");
        final Feature hs1Feature = Feature.Builder.start()
                .setId(FeatureId.from("hs_1").get())
                .setType(FeatureType.HANDSET)
                .setLabel("Handset 1")
                .build();
        GetFeaturesResult.Builder.start()
                .setId(CoreFixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .addFeature(hs1Feature)
                .build();
    }

    @Test
    public void willParseAnXmppStanza() throws Exception {

        final GetFeaturesResult result = PacketParserUtils.parseStanza(GetFeaturesFixtures.GET_FEATURES_RESULT);
        assertThat(result.getStanzaId(), is(CoreFixtures.STANZA_ID));
        assertThat(result.getTo(), is(Fixtures.TO_JID));
        assertThat(result.getFrom(), is(Fixtures.FROM_JID));
        assertThat(result.getType(), is(IQ.Type.result));
        assertThat(result.getProfileId().get(), is(CoreFixtures.PROFILE_ID));
        final List<Feature> features = result.getFeatures();
        assertThat(features.size(), is(4));
        assertThat(features.get(0).getId(), is(FeatureId.from("hs_1")));
        assertThat(features.get(0).getType().get(), is(FeatureType.HANDSET));
        assertThat(features.get(0).getLabel().get(), is("Handset 1"));

        assertThat(result.getParseErrors().size(), is(0));
    }

    @Test
    public void willReturnParsingErrors() throws Exception {

        final GetFeaturesResult result = PacketParserUtils.parseStanza(GetFeaturesFixtures.GET_FEATURES_RESULT_WITH_BAD_VALUES);

        System.out.println(result.getParseErrors());

        assertThat(result.getParseErrors(), contains(
                " Invalid get-features result; missing 'features' element is mandatory",
            //    "Invalid stanza; missing 'to' attribute is mandatory",
             //   "Invalid stanza; missing 'from' attribute is mandatory",
            //    "Invalid stanza; missing 'id' attribute is mandatory",
            //    "Invalid stanza; missing or incorrect 'type' attribute",
                "Invalid get-features result stanza; missing profile"
                ));
    }

    @Test
    public void willBuildAResultFromARequest() throws Exception {

        final GetFeaturesRequest request = GetFeaturesRequest.Builder.start()
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setId(CoreFixtures.STANZA_ID)
                .setProfileId(CoreFixtures.PROFILE_ID)
                .build();

        final GetFeaturesResult result = GetFeaturesResult.Builder.start(request)
                .build();

        assertThat(result.getStanzaId(), is(request.getStanzaId()));
        assertThat(result.getTo(), is(request.getFrom()));
        assertThat(result.getFrom(), is(request.getTo()));
    }

}
