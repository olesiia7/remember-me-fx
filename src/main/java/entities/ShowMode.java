package entities;

public enum ShowMode {
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

    ShowMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean isEnabled();

    public abstract void setEnabled(boolean isEnabled);

    public static ShowMode getModeByName(String name) {
        for (ShowMode mode : ShowMode.values()) {
            if (mode.getName().equals(name)) {
                return mode;
            }
        }
        return null;
    }

    public static void clearAllShowModeValues() {
        for (ShowMode mode : ShowMode.values()) {
            mode.setEnabled(false);
        }
    }
}
