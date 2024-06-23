package utils;

import lombok.Getter;
import models.organization.*;
import models.people.Teacher;
import models.rooms.Room;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class Solution {
    private final Instance instance;
    private final Map<Room, List<SchoolDateTime>> roomAvailability;
    private final Map<Teacher, List<SchoolDateTime>> teacherAvailability;
    private final Map<SchoolClass, List<Lesson>> timetables;

    public Solution(Instance instance){
        this.instance = instance;
        roomAvailability = new HashMap<>();
        instance.getAvailableRooms().forEach(room -> roomAvailability.put(room, new ArrayList<>()));
        teacherAvailability = new HashMap<>();
        instance.getTeachers().forEach(teacher -> teacherAvailability.put(teacher, new ArrayList<>()));
        timetables = new HashMap<>();
        instance.getSchoolClasses().forEach(schoolClass -> timetables.put(schoolClass, new ArrayList<>()));
    }

    public boolean isTeacherAvailable(Teacher teacher, SchoolDateTime schoolDateTime){
        return teacherAvailability.get(teacher).contains(schoolDateTime);
    }

    public boolean wouldTeacherExceedHours(Teacher teacher){
        // checks if teacher would exceed weekly hours after assigning another class
        return teacher.getWeeklyHours() + 1 > teacherAvailability.get(teacher).size();
    }

    public boolean isRoomAvailable(Room room, SchoolDateTime schoolDateTime){
        return roomAvailability.get(room).contains(schoolDateTime);
    }

    public boolean isFreePeriod(SchoolClass schoolClass, SchoolDateTime schoolDateTime){
        // TODO - redesign, its slow
        return timetables.get(schoolClass).stream()
                .noneMatch(lesson -> lesson.getSchoolDateTime().equals(schoolDateTime));
    }

    public boolean canAddLesson(SchoolClass schoolClass, Room room,
                                Teacher teacher, SchoolDateTime schoolDateTime){
        return isFreePeriod(schoolClass, schoolDateTime) &&
                isRoomAvailable(room, schoolDateTime) &&
                isTeacherAvailable(teacher, schoolDateTime);
    }

    public void addLesson(SchoolClass schoolClass, Course course, Room room,
                          Teacher teacher, SchoolDateTime schoolDateTime){

        Lesson lesson = new Lesson(course, room, teacher, schoolDateTime);
        roomAvailability.get(room).add(schoolDateTime);
        teacherAvailability.get(teacher).add(schoolDateTime);
        timetables.get(schoolClass).add(lesson);
    }

    public Lesson removeLesson(SchoolClass schoolClass, SchoolDateTime schoolDateTime){
        // TODO - redesign, its slow

        Lesson lesson = timetables.get(schoolClass).stream()
                .filter(l -> l.getSchoolDateTime().equals(schoolDateTime))
                .findFirst()
                .orElse(null);

        if(lesson != null){
            timetables.get(schoolClass).remove(lesson);
            roomAvailability.get(lesson.getRoom()).remove(lesson.getSchoolDateTime());
            teacherAvailability.get(lesson.getTeacher()).remove(lesson.getSchoolDateTime());
        }

        return lesson;
    }

    // TODO - Fitness functions will be changed

    public int evaluateTeacherFitness(Teacher teacher){
        var fitness = 0;
        var hours = teacherAvailability.get(teacher).size();
        fitness += (int) (-Math.pow(Math.abs(teacher.getWeeklyHours() - hours),  1.5));
        // Hole penalty
        var days = teacherAvailability.get(teacher).stream().map(SchoolDateTime::getDay).collect(Collectors.toSet());
        for (SchoolDay schoolDay : days) {
            if (teacherAvailability.get(teacher).stream()
                .filter(schoolDateTime -> schoolDateTime.getDay() == schoolDay)
                .count() >= 2) {
            var periods = teacherAvailability.get(teacher).stream()
                .filter(schoolDateTime -> schoolDateTime.getDay() == schoolDay)
                .map(SchoolDateTime::getPeriod)
                .collect(Collectors.toList());
            periods.sort(Comparator.naturalOrder());
            for (int i = 0; i < periods.size() - 1; i++) {
                if (periods.get(i + 1) - periods.get(i) > 1) {
                    fitness += periods.get(i + 1) - periods.get(i);
                }
            }
        }
        }



        return fitness;
    }

    public int evaluateClassFitness(SchoolClass schoolClass){
        if (!timetables.containsKey(schoolClass)) {
            int fitness = 0;
            Map<Course, Integer> courses = schoolClass.getCourses();

            for (Map.Entry<Course, Integer> entry : courses.entrySet()) {
                Course course = entry.getKey();
                int requiredCount = entry.getValue();
                int actualCount = (int) timetables.get(schoolClass).stream()
                        .filter(lesson -> lesson.getCourse().equals(course))
                        .count();

                if (actualCount < requiredCount) {
                    fitness += requiredCount - actualCount;
                }
            }

            return fitness;
        } else {
            return 0;
        }
    }


    // TODO fitness caching
    public int evaluateTotalTeacherFitness(){
        int fitness = 0;
//        for (Teacher teacher : teacherAvailability.keySet()) {
//            fitness += evaluateTeacherFitness(teacher);
//        }
        fitness+=fitness_Overworked_teacher();//
        fitness+=fitness_Holes_teacher();
        fitness+=fitness_WrongTeacherAssignment();
        return fitness;
    }

    public int evaluateTotalClassFitness(){
        int fitness = 0;
//        for (SchoolClass schoolClass : timetables.keySet()) {
//            fitness += evaluateClassFitness(schoolClass);
//        }
        fitness+=fitness_Overworked_class();//two courses at the same time
        fitness+=fitness_Holes_Class();//sum of holes between lessons
        fitness+=fitness_Overbooked();//two different classes have one room
        fitness+=fitness_timestartend();//lessons late
        fitness+=fitness_MissingCourses();//not all courses are
        fitness+=fitness_CoursesInWrongClassrooms();//dsnt work at the moment
        return fitness;
    }
    //fitness-count of how many errors there are
    public int fitness_Overworked_class() {
        int count = 0;
        for (SchoolClass schoolClass : timetables.keySet()) {
            List<Lesson> lessons = timetables.get(schoolClass);
            for (int i = 0; i < lessons.size(); i++) {
                Lesson lesson1 = lessons.get(i);
                for (int j = i + 1; j < lessons.size(); j++) {
                    Lesson lesson2 = lessons.get(j);
                    if (lesson1.getSchoolDateTime().equals(lesson2.getSchoolDateTime())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    public int fitness_Overworked_teacher() {
        int count = 0;
        for (Teacher teacher : teacherAvailability.keySet()) {
            List<SchoolDateTime> availableTimes = teacherAvailability.get(teacher);
            for (int i = 0; i < availableTimes.size(); i++) {
                SchoolDateTime time1 = availableTimes.get(i);
                for (int j = i + 1; j < availableTimes.size(); j++) {
                    SchoolDateTime time2 = availableTimes.get(j);
                    if (time1.equals(time2)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    public int fitness_Overbooked() {
        int count = 0;
        for (Room room : roomAvailability.keySet()) {
            List<SchoolDateTime> availableTimes = roomAvailability.get(room);
            for (int i = 0; i < availableTimes.size(); i++) {
                SchoolDateTime time1 = availableTimes.get(i);
                for (int j = i + 1; j < availableTimes.size(); j++) {
                    SchoolDateTime time2 = availableTimes.get(j);
                    if (time1.equals(time2)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
    public int fitness_Holes_Class() {
        int count = 0;
        for (SchoolClass schoolClass : timetables.keySet()) {
            List<Lesson> lessons = timetables.get(schoolClass);
            lessons.sort(Comparator.comparingInt((Lesson o) -> o.getSchoolDateTime().getDay().ordinal()).thenComparingInt(o -> o.getSchoolDateTime().getPeriod()));
            for (int i = 0; i < lessons.size() - 1; i++) {
                Lesson lesson1 = lessons.get(i);
                Lesson lesson2 = lessons.get(i + 1);
                if (lesson2.getSchoolDateTime().getDay() == lesson1.getSchoolDateTime().getDay()) {
                    int holes=lesson2.getSchoolDateTime().getPeriod() - lesson1.getSchoolDateTime().getPeriod();
                    if(holes>0)
                        count+=holes;
                }
            }
        }
        return count;
    }
    public int fitness_Holes_teacher() {
        int count = 0;
        for (Teacher teacher : teacherAvailability.keySet()) {
            List<SchoolDateTime> availableTimes = teacherAvailability.get(teacher);
            availableTimes.sort(Comparator.comparingInt((SchoolDateTime o) -> o.getDay().ordinal()).thenComparingInt(SchoolDateTime::getPeriod));
            for (int i = 0; i < availableTimes.size() - 1; i++) {
                SchoolDateTime time1 = availableTimes.get(i);
                SchoolDateTime time2 = availableTimes.get(i + 1);
                if (time2.getDay()==time1.getDay() && time2.getPeriod() - time1.getPeriod() > 1) {
                    count++;
                }
            }
        }
        return count;
    }
    public int fitness_timestartend() {
        int count = 0;
        for (SchoolClass schoolClass : timetables.keySet()) {
            List<Lesson> lessons = timetables.get(schoolClass);
            for (Lesson lesson : lessons) {
                if (lesson.getSchoolDateTime().getPeriod() >= instance.getPeriods()) {
                    count++;
                }
            }
        }
        return count;
    }
    public int fitness_MissingCourses() {
        int count = 0;
        for (SchoolClass schoolClass : timetables.keySet()) {
            List<Lesson> lessons = timetables.get(schoolClass);
            Map<Course,Integer> scheduledCourses = new HashMap<Course,Integer>();

            // Collect all scheduled courses for this class
            for (Lesson lesson : lessons) {
                if(scheduledCourses.containsKey(lesson.getCourse())){
                    scheduledCourses.put(lesson.getCourse(),scheduledCourses.get(lesson.getCourse())+1);
                }
                else
                    scheduledCourses.put(lesson.getCourse(),1);
            }

            // Get all required courses for this class
            var requiredCourses = schoolClass.getCourses();

            // Count missing courses
            for (var c : requiredCourses.keySet()) {
                if (!scheduledCourses.containsKey(c)) {
                    count++;
                }
                else{
                    if(!Objects.equals(scheduledCourses.get(c), requiredCourses.get(c)))
                        count++;
                }
            }
        }
        return count;
    }
    public int fitness_CoursesInWrongClassrooms() {
        int count = 0;
        for (SchoolClass schoolClass : timetables.keySet()) {
            List<Lesson> lessons = timetables.get(schoolClass);
            for (Lesson lesson : lessons) {
                Course course = lesson.getCourse();
                Room assignedRoom = lesson.getRoom();

                // Check if the assigned room is suitable for the course
                if (false){//assignedRoom course) {//todo, we dont simulate this,lol wf w sali komputerower
                    count++;
                }
            }
        }
        return count;
    }
    public int fitness_WrongTeacherAssignment() {
        int count = 0;
        for (SchoolClass schoolClass : timetables.keySet()) {
            List<Lesson> lessons = timetables.get(schoolClass);
            for (Lesson lesson : lessons) {
                Teacher assignedTeacher = lesson.getTeacher();
                Course subject = lesson.getCourse();

                // Check if the assigned teacher is qualified to teach the subject
                if (!assignedTeacher.canTeach(subject)) {
                    count++;
                }
            }
        }
        return count;
    }
    public int fitness_ClassChanging() {
        int count = 0;
        for (SchoolClass schoolClass : timetables.keySet()) {
            List<Lesson> lessons = timetables.get(schoolClass);
            for (int i = 0; i < lessons.size() - 1; i++) {
                Lesson lesson1 = lessons.get(i);
                Lesson lesson2 = lessons.get(i + 1);
                if (lesson1.getTeacher().equals(lesson2.getTeacher()) &&
                        !lesson1.getRoom().equals(lesson2.getRoom())) {
                    count++;
                }
            }
        }
        return count;
    }
    //fitness teacher(or students) changes classroom during the day

    private int[] fitnesses;
    public void calculateFitness(){
        fitnesses = new int[]{this.evaluateTotalTeacherFitness(),this.evaluateTotalClassFitness()};
    }

    //ea functions
    public void random() {
        timetables.clear();
        roomAvailability.clear();
        teacherAvailability.clear();

        instance.getSchoolClasses().forEach(schoolClass -> timetables.put(schoolClass, new ArrayList<>()));
        instance.getAvailableRooms().forEach(room -> roomAvailability.put(room, new ArrayList<>()));
        instance.getTeachers().forEach(teacher -> teacherAvailability.put(teacher, new ArrayList<>()));

        Random random = new Random();

        for (SchoolClass schoolClass : instance.getSchoolClasses()) {
            Map<Course, Integer> courses = schoolClass.getCourses();

            for (Map.Entry<Course, Integer> entry : courses.entrySet()) {
                Course course = entry.getKey();
                int hours = entry.getValue();

                for (int i = 0; i < hours; i++) {
                    SchoolDay day = SchoolDay.values()[random.nextInt(SchoolDay.values().length)];
                    int period = random.nextInt(instance.getPeriods());
                    SchoolDateTime schoolDateTime = new SchoolDateTime(day, period);

                    List<Teacher> competentTeachers = instance.getTeachers().stream()
                            .filter(teacher -> teacher.canTeach(course))
                            .toList();

                    if (!competentTeachers.isEmpty()) {
                        Teacher teacher = competentTeachers.get(random.nextInt(competentTeachers.size()));
                        Room room = instance.getAvailableRooms().get(random.nextInt(instance.getAvailableRooms().size()));

                        addLesson(schoolClass, course, room, teacher, schoolDateTime);
                    }
                    else{
                        //lol, schould throw error, to hire a new teacher
                    }
                }
            }
        }
    }
    public void mutate(double rate) {
        Random random = new Random();

        for (SchoolClass schoolClass : timetables.keySet()) {
            List<Lesson> lessons = timetables.get(schoolClass);

            for (Lesson lesson : lessons) {
                if (random.nextDouble() < rate) {
                    // Move the lesson to a different time
                    SchoolDateTime oldDateTime = lesson.getSchoolDateTime();
                    SchoolDateTime newDateTime = getRandomSchoolDateTime(random);
                    while (!isFreePeriod(schoolClass, newDateTime)) {
                        newDateTime = getRandomSchoolDateTime(random);
                    }
                    lesson.setSchoolDateTime(newDateTime);
                    updateAvailability(oldDateTime, newDateTime, lesson.getRoom(), lesson.getTeacher());
                }

                if (random.nextDouble() < rate) {
                    // Change the teacher assigned to the lesson
                    Course course = lesson.getCourse();
                    List<Teacher> competentTeachers = instance.getTeachers().stream()
                            .filter(teacher -> teacher.canTeach(course) && !teacher.equals(lesson.getTeacher()))
                            .toList();
                    if (!competentTeachers.isEmpty()) {
                        Teacher oldTeacher = lesson.getTeacher();
                        Teacher newTeacher = competentTeachers.get(random.nextInt(competentTeachers.size()));
                        lesson.setTeacher(newTeacher);
                        updateAvailability(lesson.getSchoolDateTime(), oldTeacher, newTeacher);
                    }
                    else{
                        //maybe hire a new teacher
                    }
                }

                if (random.nextDouble() < rate) {
                    // Change the classroom assigned to the lesson
                    Room oldRoom = lesson.getRoom();
                    List<Room> availableRooms = instance.getAvailableRooms().stream()
                            .filter(room -> !room.equals(oldRoom) && isRoomAvailable(room, lesson.getSchoolDateTime()))
                            .toList();
                    if (!availableRooms.isEmpty()) {
                        Room newRoom = availableRooms.get(random.nextInt(availableRooms.size()));
                        lesson.setRoom(newRoom);
                        updateAvailability(lesson.getSchoolDateTime(), oldRoom, newRoom);
                    }
                    else{
                        //maybe build a school before organising lessons
                    }
                }
            }
        }
    }

    private SchoolDateTime getRandomSchoolDateTime(Random random) {
        SchoolDay day = SchoolDay.values()[random.nextInt(SchoolDay.values().length)];
        int period = random.nextInt(instance.getPeriods());
        return new SchoolDateTime(day, period);
    }

    private void updateAvailability(SchoolDateTime oldDateTime, SchoolDateTime newDateTime, Room room, Teacher teacher) {
        roomAvailability.get(room).remove(oldDateTime);
        roomAvailability.get(room).add(newDateTime);
        teacherAvailability.get(teacher).remove(oldDateTime);
        teacherAvailability.get(teacher).add(newDateTime);
    }

    private void updateAvailability(SchoolDateTime dateTime, Teacher oldTeacher, Teacher newTeacher) {
        teacherAvailability.get(oldTeacher).remove(dateTime);
        teacherAvailability.get(newTeacher).add(dateTime);
    }

    private void updateAvailability(SchoolDateTime dateTime, Room oldRoom, Room newRoom) {
        roomAvailability.get(oldRoom).remove(dateTime);
        roomAvailability.get(newRoom).add(dateTime);
    }
    public static Solution cross(Solution a, Solution b, double crossRate) {
        Solution child = new Solution(a.getInstance());
        Random random = new Random();

        for (SchoolClass schoolClass : a.getTimetables().keySet()) {
            List<Lesson> lessonsA = a.getTimetables().get(schoolClass);
            List<Lesson> lessonsB = b.getTimetables().get(schoolClass);

            for (int i = 0; i < lessonsA.size(); i++) {
                if (random.nextDouble() < crossRate) {
                    child.addLesson(schoolClass, lessonsA.get(i).getCourse(), lessonsB.get(i).getRoom(),
                            lessonsA.get(i).getTeacher(), lessonsB.get(i).getSchoolDateTime());
                } else {
                    child.addLesson(schoolClass, lessonsA.get(i).getCourse(), lessonsA.get(i).getRoom(),
                            lessonsA.get(i).getTeacher(), lessonsA.get(i).getSchoolDateTime());
                }
            }
        }

        return child;
    }

    public Solution clone() {
        Solution cloned = new Solution(this.instance);

        // Clone roomAvailability
        for (Map.Entry<Room, List<SchoolDateTime>> entry : this.roomAvailability.entrySet()) {
            cloned.roomAvailability.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        // Clone teacherAvailability
        for (Map.Entry<Teacher, List<SchoolDateTime>> entry : this.teacherAvailability.entrySet()) {
            cloned.teacherAvailability.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        // Clone timetables
        for (Map.Entry<SchoolClass, List<Lesson>> entry : this.timetables.entrySet()) {
            List<Lesson> clonedLessons = entry.getValue().stream()
                    .map(lesson -> new Lesson(lesson.getCourse(), lesson.getRoom(), lesson.getTeacher(), lesson.getSchoolDateTime()))
                    .collect(Collectors.toList());
            cloned.timetables.put(entry.getKey(), clonedLessons);
        }

        // Clone fitnesses if it's not null
        if (this.fitnesses != null) {
            cloned.fitnesses = this.fitnesses.clone();
        }

        return cloned;
    }

    public Solution(Solution s){
        this.instance = s.instance;
        this.roomAvailability = new HashMap<>(s.roomAvailability);
        this.teacherAvailability = new HashMap<>(s.teacherAvailability);
        this.timetables = new HashMap<>(s.timetables);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solution solution = (Solution) o;
        return Objects.equals(roomAvailability, solution.roomAvailability) && Objects.equals(teacherAvailability, solution.teacherAvailability) && Objects.equals(timetables, solution.timetables);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomAvailability, teacherAvailability, timetables);
    }
}
