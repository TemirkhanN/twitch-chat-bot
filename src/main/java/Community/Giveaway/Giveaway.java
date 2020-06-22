package Community.Giveaway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Giveaway {
    private boolean isOver;
    private ArrayList<String> items;
    private HashSet<String> participants;
    private HashMap<String, ArrayList<String>> winners;

    public Giveaway(List<String> items) {
        this.isOver = false;
        this.participants = new HashSet<>();
        this.items = new ArrayList<>();
        this.items.addAll(items);
        this.winners = new HashMap<>();
    }

    public void addParticipant(String participant) {
        if (isOver()) {
            return;
        }

        participants.add(participant);
    }

    public List<String> getParticipants() {
        return new ArrayList<>(participants);
    }

    public boolean hasParticipant(String participant) {
        return participants.contains(participant);
    }

    public void startGiveaway() throws LogicException {
        if (isOver()) {
            throw new LogicException("Эта раздача уже завершена");
        }

        if (participants.size() == 0) {
            throw new LogicException("Недостаточно участников для начала раздачи");
        }

        ArrayList<String> participants = new ArrayList<>(this.participants);
        // Less participants than prizes. Give items by participation order.
        if (participants.size() < items.size()) {
            int currentWinnerPointer = 0;
            while(items.size() > 0) {
                String winner = participants.get(currentWinnerPointer++);
                if (currentWinnerPointer == participants.size()) {
                    currentWinnerPointer = 0;
                }
                winners.putIfAbsent(winner, new ArrayList<>());

                String item = items.get((int) (Math.random() * items.size()));
                items.remove(item);

                winners.get(winner).add(item);
            }


            isOver = true;

            return;
        }

        // More participants than prizes - give prizes randomly but only one prize in one hands
        while (participants.size() > 0 && items.size() > 0) {
            String winner = participants.get((int)(Math.random() * participants.size()));
            participants.remove(winner);
            winners.putIfAbsent(winner, new ArrayList<>());

            String item = items.get((int)(Math.random() * items.size()));
            items.remove(item);
            winners.get(winner).add(item);
        }
    }

    public boolean isOver() {
        return isOver;
    }

    public HashMap<String, ArrayList<String>> winners() {
        return winners;
    }
}
