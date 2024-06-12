import models.organization.SchoolClass;
import models.people.Teacher;
import models.rooms.*;
import utils.Generator;
import utils.Instance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Instance instance = new Instance();

        instance.exportInstance("instance.ser");

        Instance instance2 = new Instance("instance.ser");
        instance2.getSchoolClasses().forEach(System.out::println);
    }
}
