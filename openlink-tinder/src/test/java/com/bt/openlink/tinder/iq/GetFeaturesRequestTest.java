package com.bt.openlink.tinder.iq;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xmpp.packet.IQ;

import com.bt.openlink.CoreFixtures;
import com.bt.openlink.GetFeaturesFixtures;
import com.bt.openlink.tinder.Fixtures;

@SuppressWarnings("ConstantConditions")
public class GetFeaturesRequestTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void canCreateAStanza() throws Exception {

        final GetFeaturesRequest request = GetFeaturesRequest.Builder.start()
                .setId(CoreFixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setProfileId(CoreFixtures.PROFILE_ID)
                .build();

        assertThat(request.getID(), is(CoreFixtures.STANZA_ID));
        assertThat(request.getTo(), is(Fixtures.TO_JID));
        assertThat(request.getFrom(), is(Fixtures.FROM_JID));
        assertThat(request.getProfileId().get(), is(CoreFixtures.PROFILE_ID));
    }

    @Test
    public void willGenerateAnXmppStanza() throws Exception {

        final GetFeaturesRequest request = GetFeaturesRequest.Builder.start()
                .setId(CoreFixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setProfileId(CoreFixtures.PROFILE_ID)
                .build();

        assertThat(request.toXML(), isIdenticalTo(GetFeaturesFixtures.GET_FEATURES_REQUEST).ignoreWhitespace());
    }

    @Test
    public void willParseAnXmppStanza() throws Exception {

        final GetFeaturesRequest request = (GetFeaturesRequest) OpenlinkIQParser.parse(Fixtures.iqFrom(GetFeaturesFixtures.GET_FEATURES_REQUEST));
        assertThat(request.getID(), is(CoreFixtures.STANZA_ID));
        assertThat(request.getTo(), is(Fixtures.TO_JID));
        assertThat(request.getFrom(), is(Fixtures.FROM_JID));
        assertThat(request.getType(), is(IQ.Type.set));
        assertThat(request.getProfileId().get(), is(CoreFixtures.PROFILE_ID));
        assertThat(request.getParseErrors(), is(empty()));
    }

    @Test
    public void willReturnParsingErrors() throws Exception {

        final GetFeaturesRequest request = GetFeaturesRequest.from(Fixtures.iqFrom(GetFeaturesFixtures.GET_FEATURES_REQUEST_WITH_BAD_VALUES));

        assertThat(request.getParseErrors(), contains(
                "Invalid stanza; missing 'to' attribute is mandatory",
                "Invalid stanza; missing 'from' attribute is mandatory",
                "Invalid stanza; missing 'id' attribute is mandatory",
                "Invalid stanza; missing or incorrect 'type' attribute",
                "Invalid get-features request stanza; missing profile id"));
    }

    @Test
    public void willGenerateAStanzaEvenWithParsingErrors() throws Exception {

        final IQ iq = Fixtures.iqFrom(GetFeaturesFixtures.GET_FEATURES_REQUEST_WITH_BAD_VALUES);

        final GetFeaturesRequest request = GetFeaturesRequest.from(iq);

        assertThat(request.toXML(), isIdenticalTo(iq.toXML()).ignoreWhitespace());

    }

}