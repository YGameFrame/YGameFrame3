package ygame.extension.with_third_party;

import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import ygame.framework.core.YABaseDomain;

public interface YIOnContactListener
{
	void beginContact(Fixture fixture, Fixture fixtureOther,
			YABaseDomain domainOther, Contact contact);

	void endContact(Fixture fixture, Fixture fixtureOther,
			YABaseDomain domainOther, Contact contact);
}
