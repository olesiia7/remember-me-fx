package entities;

import utils.KeepDataHelper;

import java.util.List;

public enum EventDeleteMode {
    ALL("мероприятие и участников") {
        @Override
        public String getExplanation() {
            return "Удаление мерояприятия, а так же ВСЕХ людей, которые в нем состоят";
        }

        @Override
        public void provideActions(KeepDataHelper dataHelper, int eventId,
                                   List<Integer> selectedPeopleIds,
                                   List<Integer> allParticipantsIds) {
            // достаточно просто удаления людей, в events и events_and_person всё удалится каскадно
            dataHelper.deletePeople(allParticipantsIds);
        }
    },
    ONLY_EVENT("только мероприятие") {
        @Override
        public String getExplanation() {
            return "Мероприятие будет удалено из списка мероприятий ВСЕХ участников." +
                    "\nСами участники не будут удалены.";
        }

        @Override
        public void provideActions(KeepDataHelper dataHelper, int eventId,
                                   List<Integer> selectedPeopleIds,
                                   List<Integer> allParticipantsIds) {
            dataHelper.deleteEventsFromPeople(eventId, allParticipantsIds);
        }
    },
    ONLY_EVENT_FOR_CHOSEN_PARTICIPANTS("только мероприятие для выбранных участников") {
        @Override
        public String getExplanation() {
            return "Мероприятие будет удалено из списка мероприятий ВЫБРАННЫХ участников." +
                    "\nСами участники не будут удалены.";
        }

        @Override
        public void provideActions(KeepDataHelper dataHelper, int eventId,
                                   List<Integer> selectedPeopleIds,
                                   List<Integer> allParticipantsIds) {
            // удалить мероприятие только у ВЫБРАННЫХ участников!
            dataHelper.deleteEventsFromPeople(eventId, selectedPeopleIds);
        }
    },
    ONLY_PARTICIPANTS("все участники") {
        @Override
        public String getExplanation() {
            return "Удаление ВСЕХ участников мероприятия." +
                    "\nСамо мероприятие останется пустым, если Вы не добавите в него людей," +
                    "\nоно удалится автоматически";
        }

        @Override
        public void provideActions(KeepDataHelper dataHelper, int eventId,
                                   List<Integer> selectedPeopleIds,
                                   List<Integer> allParticipantsIds) {
            dataHelper.deletePeopleButLeaveEvent(allParticipantsIds);
        }
    },
    ONLY_CHOSEN_PARTICIPANTS("только выбранные участники") {
        @Override
        public String getExplanation() {
            return "Удаление только ВЫБРАННЫХ участников мероприятия." +
                    "\nЕсли мероприятие останется пустым и Вы не добавите в него людей," +
                    "\nоно удалится автоматически";
        }

        @Override
        public void provideActions(KeepDataHelper dataHelper, int eventId,
                                   List<Integer> chosenPeopleIds,
                                   List<Integer> allParticipantsIds) {
            dataHelper.deletePeopleButLeaveEvent(chosenPeopleIds);
        }
    };

    private final String name;

    EventDeleteMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getExplanation() {
        return null;
    }

    public static EventDeleteMode getModeByName(String name) {
        for (EventDeleteMode mode : EventDeleteMode.values()) {
            if (mode.getName().equals(name)) {
                return mode;
            }
        }
        return null;
    }

    public abstract void provideActions(KeepDataHelper dataHelper, int eventId,
                                        List<Integer> selectedPeopleIds,
                                        List<Integer> allParticipantsIds);
}
