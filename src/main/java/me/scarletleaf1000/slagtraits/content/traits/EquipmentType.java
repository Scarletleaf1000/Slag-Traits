package me.scarletleaf1000.slagtraits.content.traits;

public enum EquipmentType {
    TOOL("tool"),
    ARMOR("armor"),
    BOTH("both");

    private final String name;

    EquipmentType(String name) {
        this.name = name;
    }

    public static String getName(EquipmentType type) {
        return type.name;
    }

    public static EquipmentType getFromName(String s){
        for (EquipmentType type : EquipmentType.values()) {
            String name = EquipmentType.getName(type);
            if (name.equalsIgnoreCase(s)) return type;
        }

        return null;
    }
}
