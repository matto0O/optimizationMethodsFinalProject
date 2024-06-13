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

    public int evaluateTotalTeacherFitness(){
        int fitness = 0;
        for (var entry : teacherAvailability.entrySet()) {
            fitness += entry.getValue().size();
        }
        return fitness;
    }

    public int evaluateTotalClassFitness(){
        int fitness = 0;
        for (var entry : timetables.entrySet()) {
            fitness += entry.getValue().size();
        }
        return fitness;
    }
    //fitness-count of how many errors there are
    public int fitness_Overworked() {
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

    public int fitness_Holes() {
        int count = 0;
        for (SchoolClass schoolClass : timetables.keySet()) {
            List<Lesson> lessons = timetables.get(schoolClass);
            lessons.sort(Comparator.comparing(l -> l.getSchoolDateTime().getPeriod()));
            for (int i = 0; i < lessons.size() - 1; i++) {
                Lesson lesson1 = lessons.get(i);
                Lesson lesson2 = lessons.get(i + 1);
                if (lesson2.getSchoolDateTime().getPeriod() - lesson1.getSchoolDateTime().getPeriod() > 1) {
                    count++;
                }
            }
        }
        for (Teacher teacher : teacherAvailability.keySet()) {
            List<SchoolDateTime> availableTimes = teacherAvailability.get(teacher);
            availableTimes.sort(Comparator.comparing(SchoolDateTime::getPeriod));
            for (int i = 0; i < availableTimes.size() - 1; i++) {
                SchoolDateTime time1 = availableTimes.get(i);
                SchoolDateTime time2 = availableTimes.get(i + 1);
                if (time2.getPeriod() - time1.getPeriod() > 1) {
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
                if (lesson.getSchoolDateTime().getPeriod() >= instance.getPeriods() - 1) {
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

    //fitness class missing courses
    //fitness courses in wrong classrooms


    private int[] fitnesses;
    public void calculateFitness(){
        fitnesses = new int[]{this.fitness_Overworked(), this.fitness_Overbooked(), this.fitness_Holes(), this.fitness_timestartend(), this.fitness_ClassChanging()};
    }

    //ea functions
    public void random() {
        // Clear existing timetables and availability
        timetables.clear();
        roomAvailability.clear();
        teacherAvailability.clear();

        // Initialize timetables and availability
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
                        //lol, schould throw error
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
                    Lesson lessonA = lessonsA.get(i);
                    Lesson lessonB = lessonsB.get(i);

                    // Swap the lessons between solutions
                    SchoolDateTime dateTimeA = lessonA.getSchoolDateTime();
                    SchoolDateTime dateTimeB = lessonB.getSchoolDateTime();
                    Room roomA = lessonA.getRoom();
                    Room roomB = lessonB.getRoom();
                    Teacher teacherA = lessonA.getTeacher();
                    Teacher teacherB = lessonB.getTeacher();

                    if (child.canAddLesson(schoolClass, roomB, teacherB, dateTimeB)) {
                        child.addLesson(schoolClass, lessonB.getCourse(), roomB, teacherB, dateTimeB);
                    } else {
                        child.addLesson(schoolClass, lessonA.getCourse(), roomA, teacherA, dateTimeA);
                    }
                } else {
                    child.addLesson(schoolClass, lessonsA.get(i).getCourse(), lessonsA.get(i).getRoom(),
                            lessonsA.get(i).getTeacher(), lessonsA.get(i).getSchoolDateTime());
                }
            }
        }

        return child;
    }
}
