package mnomoko.android.com.happyweather.database;

/**
 * Created by mnomoko on 05/07/15.
 */
public class City {

    private int id;
    private String name;
    private String code;

    public City() {}

    public City(String n, String c) {
        super();
        name = n;
        code = c;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "City [id = " + id + ", name = + " + name + ", code = " + code + "]";
    }
}
