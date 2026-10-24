package marsops.facilities;

public class Equipment {
    private String equipmentName;
    private String operationalStatus;

    public Equipment(String equipmentName , String operationalStatus){
        setEquipmentName(equipmentName);
        setOperationalStatus(operationalStatus);
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getOperationalStatus() {
        return operationalStatus;
    }

    public void setEquipmentName(String equipmentName) {
        if(equipmentName == null || equipmentName.isBlank())
            throw new IllegalArgumentException("equipment can't be empty!");
       else this.equipmentName = equipmentName;
    }

    public void setOperationalStatus(String operationalStatus) {
        if(operationalStatus == null || operationalStatus.isBlank())
            throw new IllegalArgumentException("operation status can't be empty!");
        else this.operationalStatus = operationalStatus;
    }
}
