package utils;

import lombok.Getter;
import models.organization.Course;
import models.organization.Lesson;
import models.organization.SchoolClass;
import models.organization.SchoolDateTime;
import models.people.Teacher;
import models.rooms.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return teacherAvailability.get(teacher).size();
    }

    public int evaluateClassFitness(SchoolClass schoolClass){
        return timetables.get(schoolClass).size();
    }

    public int evaluateTotalTeacherFitness(){
        int fitness = 0;
        for (Map.Entry<Teacher, List<SchoolDateTime>> entry : teacherAvailability.entrySet()) {
            fitness += entry.getValue().size();
        }
        return fitness;
    }

    public int evaluateTotalClassFitness(){
        int fitness = 0;
        for (Map.Entry<SchoolClass, List<Lesson>> entry : timetables.entrySet()) {
            fitness += entry.getValue().size();
        }
        return fitness;
    }
}
