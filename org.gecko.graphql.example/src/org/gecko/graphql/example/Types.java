package org.gecko.graphql.example;

import org.gecko.graphql.ScalarTypes;
import org.gecko.graphql.example.model.Anniversary;
import org.gecko.graphql.example.model.AnniversaryType;
import org.gecko.graphql.example.model.Contact;
import org.gecko.graphql.example.model.Gender;
import org.gecko.graphql.example.model.Person;
import org.gecko.graphql.example.model.PersonStatus;
import org.gecko.graphql.example.model.PersonStatusType;
import org.gecko.graphql.example.model.Phone;
import org.gecko.graphql.example.model.PhoneType;
import org.gecko.graphql.example.model.Presence;
import org.gecko.graphql.example.model.PresenceType;
import org.gecko.graphql.example.model.Realm;

import graphql.Scalars;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLNonNull;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLOutputType;

public interface Types {

	public static final String INPUT_SUFFIX = "Input";

	public static final GraphQLEnumType REALM = GraphQLEnumType.newEnum()
			.name(Realm.class.getSimpleName())
			.value(Realm.PRIVATE.name())
			.value(Realm.PUBLIC.name())
			.build();

	public static final GraphQLOutputType ANNIVERSARY_TYPE = GraphQLObjectType.newObject()
			.name(AnniversaryType.class.getSimpleName())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(AnniversaryType.Fields.ID)
							.type(GraphQLNonNull.nonNull(Scalars.GraphQLID))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(AnniversaryType.Fields.REALM)
							.type(REALM)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(AnniversaryType.Fields.TITLE)
							.type(Scalars.GraphQLString)
							.build())
			.build();

	public static final GraphQLOutputType ANNIVERSARY = GraphQLObjectType.newObject()
			.name(Anniversary.class.getSimpleName())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Anniversary.Fields.DATE)
							.type(ScalarTypes.LOCAL_DATE_ISO_8601)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Anniversary.Fields.DESCRIPTION)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Anniversary.Fields.ID)
							.type(GraphQLNonNull.nonNull(Scalars.GraphQLID))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Anniversary.Fields.TYPE)
							.type(ANNIVERSARY_TYPE)
							.build())
			.build();

	public static final GraphQLEnumType PHONE_TYPE = GraphQLEnumType.newEnum()
			.name(PhoneType.class.getSimpleName())
			.value(PhoneType.FAX.name())
			.value(PhoneType.LANDLINE.name())
			.value(PhoneType.MOBILE.name())
			.build();

	public static final GraphQLOutputType PHONE = GraphQLObjectType.newObject()
			.name(Phone.class.getSimpleName())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Phone.Fields.ID)
							.type(GraphQLNonNull.nonNull(Scalars.GraphQLID))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Phone.Fields.LABEL)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Phone.Fields.NUMBER)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Phone.Fields.PRIMARY)
							.type(Scalars.GraphQLBoolean)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Phone.Fields.TYPE)
							.type(PHONE_TYPE)
							.build())
			.build();

	public static final GraphQLOutputType CONTACT = GraphQLObjectType.newObject()
			.name(Contact.class.getSimpleName())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Contact.Fields.CITY)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Contact.Fields.COUNTRY)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Contact.Fields.EMAIL)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Contact.Fields.ID)
							.type(GraphQLNonNull.nonNull(Scalars.GraphQLID))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Contact.Fields.PHONES)
							.type(GraphQLList.list(PHONE))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Contact.Fields.POSTAL_CODE)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Contact.Fields.REALM)
							.type(REALM)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Contact.Fields.RECIPIENT)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Contact.Fields.STREET)
							.type(Scalars.GraphQLString)
							.build())
			.build();

	public static final GraphQLEnumType GENDER = GraphQLEnumType.newEnum()
			.name(Gender.class.getSimpleName())
			.value(Gender.DIVERSE.name())
			.value(Gender.FEMALE.name())
			.value(Gender.MALE.name())
			.build();

	public static final GraphQLEnumType PRESENCE_TYPE = GraphQLEnumType.newEnum()
			.name(PresenceType.class.getSimpleName())
			.value(PresenceType.AWAY.name())
			.value(PresenceType.DND.name())
			.value(PresenceType.NA.name())
			.value(PresenceType.OFFLINE.name())
			.value(PresenceType.ONLINE.name())
			.build();

	public static final GraphQLOutputType PRESENCE = GraphQLObjectType.newObject()
			.name(Presence.class.getSimpleName())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Presence.Fields.MESSAGE)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Presence.Fields.SINCE)
							.type(ScalarTypes.LOCAL_DATE_TIME_ISO_8601)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Presence.Fields.STATUS)
							.type(GraphQLNonNull.nonNull(PRESENCE_TYPE))
							.build())
			.build();

	public static GraphQLInputType PRESENCE_INPUT = GraphQLInputObjectType.newInputObject()
			.name(Presence.class.getSimpleName() + INPUT_SUFFIX)
			.field(
					GraphQLInputObjectField.newInputObjectField()
							.name(Presence.Fields.MESSAGE)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLInputObjectField.newInputObjectField()
							.name(Presence.Fields.STATUS)
							.type(GraphQLNonNull.nonNull(PRESENCE_TYPE))
							.build())
			.build();

	public static final GraphQLEnumType PERSON_STATUS_TYPE = GraphQLEnumType.newEnum()
			.name(PersonStatusType.class.getSimpleName())
			.value(PersonStatusType.ABSENT.name())
			.value(PersonStatusType.ACTIVE.name())
			.value(PersonStatusType.BLOCKED.name())
			.value(PersonStatusType.DECEASED.name())
			.value(PersonStatusType.INCOMING.name())
			.value(PersonStatusType.OUTGOING.name())
			.value(PersonStatusType.PRESENT.name())
			.build();

	public static final GraphQLOutputType PERSON_STATUS = GraphQLObjectType.newObject()
			.name(PersonStatus.class.getSimpleName())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(PersonStatus.Fields.END)
							.type(ScalarTypes.LOCAL_DATE_ISO_8601)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(PersonStatus.Fields.ID)
							.type(GraphQLNonNull.nonNull(Scalars.GraphQLID))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(PersonStatus.Fields.START)
							.type(ScalarTypes.LOCAL_DATE_ISO_8601)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(PersonStatus.Fields.TITLE)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(PersonStatus.Fields.TYPE)
							.type(PERSON_STATUS_TYPE)
							.build())
			.build();

	public static final GraphQLOutputType PERSON = GraphQLObjectType.newObject()
			.name(Person.class.getSimpleName())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Person.Fields.ANNIVERSARIES)
							.type(GraphQLList.list(ANNIVERSARY))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Person.Fields.CONTACTS)
							.type(GraphQLList.list(CONTACT))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Person.Fields.GENDER)
							.type(GENDER)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Person.Fields.ID)
							.type(GraphQLNonNull.nonNull(Scalars.GraphQLID))
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Person.Fields.PREFERRED_NAME)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Person.Fields.PRESENCE)
							.type(PRESENCE)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Person.Fields.STATUS)
							.type(PERSON_STATUS)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Person.Fields.SURNAME)
							.type(Scalars.GraphQLString)
							.build())
			.field(
					GraphQLFieldDefinition.newFieldDefinition()
							.name(Person.Fields.USER_NAME)
							.type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
							.build())
			.build();
}
