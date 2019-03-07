package geb.com.intershop.b2x.model.storefront.responsive;

/**
 * This class represents a domain object with ID and name.
 */
abstract class NamedObject
{
    String id;

    String name;

    /**
     * Constructor
     */
    NamedObject(String id, String name)
    {
        assert id != null;
        assert name != null;

        this.id = id;
        this.name = name;
    }

    String toString()
    {
        StringBuilder buf = new StringBuilder(getClass().getSimpleName())
        buf.append("[")
        buf.append(name)
        buf.append(", ")
        buf.append(id)
        buf.append("]")

        buf.toString()
    }
}





