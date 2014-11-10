package ch.ethz.itet.pps.budgetSplit;

import java.util.ArrayList;

/**
 * Created by Chrissy on 02.11.2014.
 */
public class GlobalStuffHelper {

    static long virtualCounter = 15;
    static ArrayList<Long> participantIds = new ArrayList<Long>(); // still needs to be properly implemented
    static ArrayList<Long> participantIdsToProject = new ArrayList<Long>();

    public static void raiseVirtualCounterByOne() {
        virtualCounter++;
    }

    public static long getVirtualCounter() {
        return virtualCounter;
    }

    public static void addParticipantsIds(long value) {
        participantIds.add(value);
    }

    public static void addParticipantIdsToProject(long value) {
        participantIdsToProject.add(value);
    }

    public static long popParticipantIds(int position) {
        long memory = participantIds.get(position);
        participantIds.remove(position);
        return memory;
    }

    public static long popParticipantIds(long id) {
        long memory = participantIds.get(participantIds.indexOf(id));
        participantIds.remove(id);
        return memory;
    }

    public static long popParticipantIdsToProject(int position) {
        long memory = participantIdsToProject.get(position);
        participantIdsToProject.remove(position);
        return memory;
    }

    public static long getParticipantIds(int position) {
        return participantIds.get(position);
    }

    public static long getParticipantIdsToProject(int position) {
        return participantIdsToProject.get(position);
    }

    public static void clearSpinnerArray() {
        participantIds.clear();
    }

    public static void clearListArray() {
        participantIdsToProject.clear();
    }


    public static int sizeParticipantIdsToProject() {
        return participantIdsToProject.size();
    }
}
