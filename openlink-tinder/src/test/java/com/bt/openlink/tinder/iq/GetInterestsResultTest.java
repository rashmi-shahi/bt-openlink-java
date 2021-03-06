package com.bt.openlink.tinder.iq;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.xmpp.packet.IQ;

import com.bt.openlink.CoreFixtures;
import com.bt.openlink.GetInterestsFixtures;
import com.bt.openlink.tinder.Fixtures;
import com.bt.openlink.type.Interest;
import com.bt.openlink.type.InterestId;
import com.bt.openlink.type.InterestType;

@SuppressWarnings({ "OptionalGetWithoutIsPresent", "ConstantConditions" })
public class GetInterestsResultTest {

    @Rule public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void canCreateAStanza() throws Exception {

        final GetInterestsResult result = GetInterestsResult.Builder.start()
                .setId(CoreFixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .addInterest(CoreFixtures.INTEREST)
                .build();

        assertThat(result.getType(), is(IQ.Type.result));
        assertThat(result.getID(), is(CoreFixtures.STANZA_ID));
        assertThat(result.getTo(), is(Fixtures.TO_JID));
        assertThat(result.getFrom(), is(Fixtures.FROM_JID));
        assertThat(result.getInterests().get(0), is(CoreFixtures.INTEREST));
    }

    @Test
    public void willGenerateAnXmppStanza() throws Exception {

        final Interest interest2 = Interest.Builder.start()
                .setId(InterestId.from("sip:6001@uta.bt.com-DirectDial-1trader1@btsm11").get())
                .setType(InterestType.from("DirectoryNumber").get())
                .setLabel("6001/1")
                .setDefault(false)
                .build();
        final GetInterestsResult result = GetInterestsResult.Builder.start()
                .setId(CoreFixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .addInterest(CoreFixtures.INTEREST)
                .addInterest(interest2)
                .build();

        assertThat(result.toXML(), isIdenticalTo(GetInterestsFixtures.GET_INTERESTS_RESULT).ignoreWhitespace());
    }

    @Test
    public void willNotBuildAPacketWithDuplicateInterestIds() throws Exception {

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Each interest id must be unique - test-interest-id appears more than once");
        GetInterestsResult.Builder.start()
                .setId(CoreFixtures.STANZA_ID)
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .addInterest(CoreFixtures.INTEREST)
                .addInterest(CoreFixtures.INTEREST)
                .build();
    }

    @Test
    public void willParseAnXmppStanza() throws Exception {

        final GetInterestsResult result = (GetInterestsResult) OpenlinkIQParser.parse(Fixtures.iqFrom(GetInterestsFixtures.GET_INTERESTS_RESULT));
        assertThat(result.getID(), is(CoreFixtures.STANZA_ID));
        assertThat(result.getTo(), is(Fixtures.TO_JID));
        assertThat(result.getFrom(), is(Fixtures.FROM_JID));
        assertThat(result.getType(), is(IQ.Type.result));
        final List<Interest> interests = result.getInterests();

        int i = 0;
        Interest interest = interests.get(i++);
        assertThat(interest.getId().get(),is(CoreFixtures.INTEREST_ID));
        assertThat(interest.getLabel().get(),is("test interest label"));
        assertThat(interest.getType(),is(InterestType.from("test-interest-type")));

        interest = interests.get(i++);
        assertThat(interest.getId(), is(InterestId.from("sip:6001@uta.bt.com-DirectDial-1trader1@btsm11")));
        assertThat(interest.getType(), is(InterestType.from("DirectoryNumber")));
        assertThat(interest.getLabel().get(), is("6001/1"));
        assertThat(interest.isDefaultInterest().get(), is(false));

        assertThat(interests.size(), is(i));

        assertThat(result.getParseErrors().size(), is(0));
    }

    @Test
    public void willReturnParsingErrors() throws Exception {

        final GetInterestsResult result = GetInterestsResult.from(Fixtures.iqFrom(GetInterestsFixtures.GET_INTERESTS_RESULT_WITH_BAD_VALUES));

        assertThat(result.getParseErrors(), contains(
                "Invalid stanza; missing 'to' attribute is mandatory",
                "Invalid stanza; missing 'from' attribute is mandatory",
                "Invalid stanza; missing 'id' attribute is mandatory",
                "Invalid stanza; missing or incorrect 'type' attribute"
        ));
    }

    @Test
    public void willBuildAResultFromARequest() throws Exception {

        final GetInterestsRequest request = GetInterestsRequest.Builder.start()
                .setTo(Fixtures.TO_JID)
                .setFrom(Fixtures.FROM_JID)
                .setId(CoreFixtures.STANZA_ID)
                .setProfileId(CoreFixtures.PROFILE_ID)
                .build();

        final GetInterestsResult result = GetInterestsResult.Builder.start(request)
                .build();

        assertThat(result.getID(), is(request.getID()));
        assertThat(result.getTo(), is(request.getFrom()));
        assertThat(result.getFrom(), is(request.getTo()));
    }

}
