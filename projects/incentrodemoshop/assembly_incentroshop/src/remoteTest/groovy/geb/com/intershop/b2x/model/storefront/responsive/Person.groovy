package geb.com.intershop.b2x.model.storefront.responsive

/**
 * This class represents a person.
 */
class Person
{
    private String firstName;

    private String lastName;

    /**
     * Constructor for Person.
     */
    Person(String firstName, String lastName)
    {
        assert firstName != null;
        assert lastName != null;

        this.firstName = firstName;
        this.lastName = lastName;
    }

    String getName() {
        return firstName + " " + lastName;
    }

    String getFirstName() {
       firstName
    }

    String getLastName() {
        lastName
    }
}





