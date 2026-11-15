# MARS-OPS — Autonomous Mars Colony Management & Emergency Simulator

MARS-OPS is a Java object-oriented simulation of a Mars research colony. It models human personnel, autonomous robotic units, facilities, shared resources, missions, environmental conditions, and emergency responses through collaborating classes.

---

## 1. System Architecture & Main Components

The codebase is organized under `src/marsops/`:

- **`marsops.colony`**
  - `MarsColony`: Central coordinator holding crew members, robotic units, facilities, current `EnvironmentalCondition`, and the `activeMission`. Coordinates mission submission/advancement, colony-wide resource checks/consumption, and resource reporting.
  - `EnvironmentalCondition`: Enum (`NORMAL`, `DUST_STORMS`, `COMMUNICATION_DISRUPTION`).
- **`marsops.personnel`**
  - `CrewMember` (abstract): Base class for human personnel (`name`, `id`, `healthStatus`, `currentLocation`, `fatigueLevel`, `available`), declaring `performDuty()`, `canHandle(EmergencyType)`, and `canPerform(Mission)`.
  - Subclasses: `Scientist`, `Engineer`, `MedicalOfficer`, `FireFighter`.
- **`marsops.robotics`**
  - `Robot` (abstract): Base class for autonomous units (`batteryLevel`, `healthStatus`, `currentLocation`, `available`), declaring `performTask()`, `canHandle(EmergencyType)`, and `canPerform(Mission)`.
  - Subclasses: `ExplorationRover`, `RepairRobot`, `Drone`.
- **`marsops.facilities`**
  - `Facility` (abstract): Base class for colony structures (`facilityLocation`, `capacity`, `facilityOperationalStatus`, `resources`, `equipmentList`).
  - Subclasses: `Habitat` (with `LifeSupportSystem`), `Laboratory`, `StorageFacility`, `PowerStation`, `CommunicationStation`.
  - `Resource`, `ResourceType` (enum), `Equipment`.
- **`marsops.missions`**
  - `Mission` (abstract): Base class managing mission identity, duration, `MissionState`, assigned agents, validation, time progression, and failure handling.
  - Subclasses: `ExplorationMission`, `ResearchMission`, `RepairMission`, `RescueMission`.
- **`marsops.emergencies`**
  - `Emergency` (abstract): Base class with `Severity`, `Status`, `EmergencyType`, and `respond(MarsColony)`.
  - Subclasses: `OxygenLeak`, `FireEmergency`, `MedicalEmergency`, `RoverBreakdown`.
- **`marsops.Main`**
  - Entry point demonstrating initialization, resource management, all emergency types, environmental effects, and all mission scenarios.

---

## 2. Design Decisions & Assumptions

- **Polymorphic Capability Dispatch (`canPerform` / `canHandle`):** Rather than using `instanceof` checks inside `Mission` or `Emergency`, each `CrewMember` and `Robot` subclass decides whether it can perform a given `Mission` or handle a given `EmergencyType`.
- **Single Active Mission:** `MarsColony` allows at most one `activeMission` at a time (`submitMission` immediately rejects a new mission with `FAILED` if another mission is active).
- **No Retries on Failed Missions:** Once a mission enters `FAILED`, it cannot be resubmitted; a new `Mission` instance must be created.
- **Agent Reservation & Release:** When a mission passes validation and transitions to `IN_PROGRESS`, its assigned crew and robots are marked `available = false`. When the mission transitions to `COMPLETED` or `FAILED`, `releaseAgents()` restores `available = true`.
- **Location-Independent Emergency Dispatch:** Any usable and available responder matching the emergency type may respond regardless of current location.

---

## 3. Resource Allocation Rules

1. **Colony-Wide Totals:** `MarsColony.getTotalResourceQuantity(ResourceType)` sums quantities across all facilities.
2. **Multi-Source Consumption:** `MarsColony.consumeResource(ResourceType, int)` draws from multiple facilities sequentially until the full requested quantity is satisfied.
3. **All-or-Nothing Consumption:** `hasSufficientResource` is checked before any deduction; if total available quantity is insufficient, an `IllegalStateException` is thrown and no facility's resource count is modified or allowed to go negative.
4. **Local Emergencies:** `OxygenLeak` deducts oxygen specifically from the affected `Habitat` (with additional loss if unresolved).
5. **Status Report:** `MarsColony.printResourceReport()` prints the current `EnvironmentalCondition` and colony-wide totals for every `ResourceType`.

---

## 4. Emergency Rules

- **`OxygenLeak`:** Reduces oxygen in the target habitat, requires an `Engineer` and `RepairRobot` plus spare parts and battery power; if unresolved, applies additional oxygen loss and degrades habitat status.
- **`FireEmergency`:** Requires a `FireFighter` plus water and medical supplies; if unresolved, damages facility/equipment and may injure personnel at that location.
- **`MedicalEmergency`:** Requires a `MedicalOfficer` plus medical supplies and oxygen; restores patient health when resolved.
- **`RoverBreakdown`:** Requires an `Engineer` and `RepairRobot` plus spare parts and battery power; restores rover health and availability when resolved.
- Responders must satisfy `isUsableAndAvailable()` and `canHandle(EmergencyType)`.

---

## 5. Environmental Effects

- **`NORMAL`:** All surface exploration, drone control, and communication-dependent operations are permitted.
- **`DUST_STORMS`:** Surface exploration missions cannot start (`canStartSurfaceExploration() == false`), remote drones cannot be controlled (`canControlRemoteDrones() == false`), and active rover/surface missions fail immediately via `onEnvironmentChanged(MarsColony)`.
- **`COMMUNICATION_DISRUPTION`:** Remote drones cannot be controlled and communication-dependent operations are restricted (`isCommunicationAvailable() == false`).

---

## 6. Mission States

`Mission.MissionState` defines four states:
- **`ASSIGNED`:** Initial state upon mission creation.
- **`IN_PROGRESS`:** Entered when `validateAndPerformChecks(MarsColony)` succeeds (environment, agent capability/availability, resource availability, and mission-specific checks pass, resources are consumed, and agents are reserved).
- **`COMPLETED`:** Entered when `advanceTime(MarsColony)` reaches `elapsedDuration >= requiredDuration`; assigned agents are released.
- **`FAILED`:** Entered if submission validation fails or a running condition fails (e.g., dust storm during surface exploration); `failureReason` is recorded and any reserved agents are released.

---

## 7. Compiling and Running

### Using the Run Script (Linux/macOS)
```bash
./run.sh
```

### Manual Compilation & Execution
```bash
mkdir -p out
javac -d out $(find src -name "*.java")
java -cp out marsops.Main
```

---

## 8. Executing the Required Demonstration Scenarios

Running `marsops.Main` (via `./run.sh` or `java -cp out marsops.Main`) executes all required scenarios in order:

1. **Scenario 1 — Successful Exploration:** Section `8` (`EXP-01`) initializes a `Scientist`, `ExplorationRover`, and `Spectrometer` equipment in `Lab-1`, submits `ExplorationMission`, advances simulation time to completion, and reports `COMPLETED (2/2)`.
2. **Scenario 2 — Mission Rejection (Unsuitable Agent):** Section `8` (`EXP-UNSUITABLE`) assigns a `FireFighter` to an `ExplorationMission`, which is immediately rejected as `FAILED` with the capability reason.
3. **Scenario 3 — Resource Shortage:** Section `2` attempts to consume `25` units of `OXYGEN` when only `15` remain, demonstrating all-or-nothing rejection without negative quantities.
4. **Scenario 4 — Emergency Handling:** Sections `3`–`6` trigger `OxygenLeak`, `MedicalEmergency`, `FireEmergency`, and `RoverBreakdown` (both resolved and unresolved cases), updating facility, agent, and resource states.
5. **Scenario 5 — Environmental Effect:** Sections `7` and `8` (`EXP-02`) set `EnvironmentalCondition.DUST_STORMS` and demonstrate that surface exploration cannot start (`FAILED`).
