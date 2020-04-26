package entities;

public enum ShowModeEnum {
    NAME("ФИО") {
        private boolean enabled = false;

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean isEnabled) {
            this.enabled = isEnabled;
        }
    },
    EVENTS("Мероприятия") {
        private boolean enabled = false;

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean isEnabled) {
            this.enabled = isEnabled;
        }
    },
    COMPANY("Место работы") {
        private boolean enabled = false;

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean isEnabled) {
            this.enabled = isEnabled;
        }
    },
    ROLE("Должность") {
        private boolean enabled = false;

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean isEnabled) {
            this.enabled = isEnabled;
        }
    },
    DESCRIPTION("Описание") {
        private boolean enabled = false;

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean isEnabled) {
            this.enabled = isEnabled;
        }
    },
    PICTURES("Фотографии") {
        private boolean enabled = false;

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void setEnabled(boolean isEnabled) {
            this.enabled = isEnabled;
        }
    };

    private final String name;

    ShowModeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean isEnabled);

    public static ShowModeEnum getModeByName(String name) {
        for (ShowModeEnum mode : ShowModeEnum.values()) {
            if (mode.getName().equals(name)) {
                return mode;
            }
        }
        return null;
    }

    public static void clearAllShowModeValues() {
        for (ShowModeEnum mode : ShowModeEnum.values()) {
            mode.setEnabled(false);
        }
    }
}
