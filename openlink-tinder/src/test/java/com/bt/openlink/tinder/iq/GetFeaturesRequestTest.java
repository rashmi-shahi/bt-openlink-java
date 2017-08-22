package com.bt.openlink.tinder.iq;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xmpp.packet.IQ;

import com.bt.openlink.tinder.Fixtures;

@SuppressWarnings("ConstantConditions")
public class GetFeaturesRequestTest {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private static final String GET_FEATURES_REQUEST = "<iq type=\"set\" id=\"" + Fixtures.STANZA_ID + "\" to=\"" + Fixtures.TO_JID + "\" from=\"" + Fixtures.FROM_JID + "\">\n" +
            "  <command xmlns=\"http://jabber.org/protocol/commands\" node=\"http://xmpp.org/protocol/openlink:01:00:00#get-features\" action=\"execute\">\n" +
            "    <iodata xmlns=\"urn:xmpp:tmp:io-data\" type=\"input\">\n" +
            "      <in>\n" +
            "        <profile>" + Fixtures.PROFILE_ID + "</profile>\n" +
            "      </in>\n" +
            "    </iodata>\n" +
            "  </command>\n" +
            "</iq>\n";

    private static final String GET_FEATURES_REQUEST_WITH_BAD_VALUES = "<iq>\n" +
            "  <command xmlns=\"http://jabber.org/protocol/commands\" action=\"execute\" node=\"http://xmpp.org/protocol/openlink:01:00:00#get-features\">\n" +
            "    <iodata xmlns=\"urn:xmpp:tmp:io-data\" type=\"input\">\n" +
            "      <in/>\n" +
            "    </iodata>\n" +
            "  </command>\n" +
            "</iq>\n";

    @Test
    public void canCreateAStanza() throws Exception {

        final GetFeaturesRequest request = GetFeaturesRequest.Builder.start()
                .setID(Fixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setProfileId(Fixtures.PROFILE_ID)
                .build();

        assertThat(request.getID(), is(Fixtures.STANZA_ID));
        assertThat(request.getTo(), is(Fixtures.TO_JID));
        assertThat(request.getFrom(), is(Fixtures.FROM_JID));
        assertThat(request.getProfileId().get(), is(Fixtures.PROFILE_ID));
    }

    @Test
    public void cannotCreateAStanzaWithoutAToField() throws Exception {

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The stanza 'to' has not been set");
        GetFeaturesRequest.Builder.start()
                .build();
    }

    @Test
    public void cannotCreateAStanzaWithoutAProfileId() throws Exception {

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The profileId has not been set");
        GetFeaturesRequest.Builder.start()
                .setTo(Fixtures.TO_JID)
                .build();
    }

    @Test
    public void willGenerateAnXmppStanza() throws Exception {

        final GetFeaturesRequest request = GetFeaturesRequest.Builder.start()
                .setID(Fixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setProfileId(Fixtures.PROFILE_ID)
                .build();

        assertThat(request.toXML(), isIdenticalTo(GET_FEATURES_REQUEST).ignoreWhitespace());
    }

    @Test
    public void willGenerateAnXmppStanzaWithARandomId() throws Exception {

        final GetFeaturesRequest request = GetFeaturesRequest.Builder.start()
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setProfileId(Fixtures.PROFILE_ID)
                .build();

        assertThat(request.getID(), is(not(nullValue())));
    }

    @Test
    public void willParseAnXmppStanza() throws Exception {

        final GetFeaturesRequest request = (GetFeaturesRequest) OpenlinkIQParser.parse(Fixtures.iqFrom(GET_FEATURES_REQUEST));
        assertThat(request.getID(), is(Fixtures.STANZA_ID));
        assertThat(request.getTo(), is(Fixtures.TO_JID));
        assertThat(request.getFrom(), is(Fixtures.FROM_JID));
        assertThat(request.getType(), is(IQ.Type.set));
        assertThat(request.getProfileId().get(), is(Fixtures.PROFILE_ID));
        assertThat(request.getParseErrors(), is(empty()));
    }

    @Test
    public void willReturnParsingErrors() throws Exception {

        final IQ iq = Fixtures.iqFrom(GET_FEATURES_REQUEST_WITH_BAD_VALUES);

        final GetFeaturesRequest request = GetFeaturesRequest.from(iq);

        assertThat(request.getParseErrors(), contains(
                "Invalid stanza; missing or incorrect 'type' attribute",
                "Invalid stanza; missing 'to' attribute is mandatory",
                "Invalid stanza; missing 'from' attribute is mandatory",
                "Invalid stanza; missing 'id' attribute is mandatory",
                "Invalid get-features request; missing 'profile' field is mandatory"));
    }

    @Test
    public void willGenerateAStanzaEvenWithParsingErrors() throws Exception {

        final IQ iq = Fixtures.iqFrom(GET_FEATURES_REQUEST_WITH_BAD_VALUES);

        final GetFeaturesRequest request = GetFeaturesRequest.from(iq);

        assertThat(request.toXML(), isIdenticalTo(iq.toXML()).ignoreWhitespace());

    }

}