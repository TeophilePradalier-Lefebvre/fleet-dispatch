# FleetDispatch

FleetDispatch is a Java application that dispatches deliveries. Parcels come in and a small fleet waits at the depot: drones, vans and electric cargo bikes. The application picks a vehicle for each parcel. It computes the price. It cleanly refuses the cases that cannot work.

This is my final Object-Oriented Programming project (M1). I started from the delivery example seen in class (the `DeliveryPolicy`). I wanted a subject with real business rules that would not grow out of hand. Every class should be explainable in one sentence.

## Running the project

You need JDK 21 and Maven. IntelliJ handles both.

```bash
git clone https://github.com/TeophilePradalier-Lefebvre/fleet-dispatch.git
cd fleet-dispatch
mvn compile
java -cp target/classes com.junia.fleetdispatch.Application
```

Run it from the project root because the data files are read with relative paths. In IntelliJ the green triangle next to the `main` of `Application` does the same thing.

The program prints two dispatch rounds and then a financial report. It also prints the messages meant for customers and the audit log.

## Features

A parcel has a weight and a zone. It can also be fragile, express or insured. Here is what happens when it is dispatched.

1. The zone gives a distance. The existing distance system speaks in miles and an adapter converts to kilometres.
2. The service keeps the vehicles able to do the delivery. They must be free and strong enough. Their range must cover the round trip since the vehicle goes back to the depot. They must also accept the parcel: a drone refuses a fragile parcel over 2 kg for example.
3. A strategy picks among the candidates. Either the cheapest per kilometre or the smallest one that fits.
4. The price is computed. The vehicle becomes busy and loses range.

The price is made of a 3 € base fee, 0.80 € per km and 0.10 € per kg. A fragile parcel adds 5 € and insurance costs 2 % of the declared value. For express the total is multiplied by 1.5 surcharges included.

When a delivery ends the vehicle is released and then recharged or refuelled. The subscribers (the *listeners*) are notified. The report gives the revenue, the cost, the margin, the fleet utilisation rate and the most profitable parcel. Only delivered parcels count. A parcel that is assigned but has not arrived yet is not revenue.

An impossible parcel does not stop the batch. The failure is recorded with its reason and the other parcels carry on.

The demo handles eight parcels. In the first round six are assigned and two are refused. `P-006` weighs 1,500 kg while the fleet tops out at 1,200 kg. `P-008` shows up when no vehicle is free anymore. In the second round the vehicles are back. The refused parcels are retried with the other strategy: `P-008` is delivered by `DR-01` and `P-006` fails again.

## Main classes

| Package | Contents |
|---|---|
| `model` | `Vehicle` (abstract), `Drone`, `Van`, `CargoBike`, `Parcel` (with its Builder), `Delivery`, and the interfaces `Rechargeable` and `Refuelable` |
| `pricing` | `PricingPolicy`, `StandardPricing`, `ExpressPricing`, `PricingPolicyFactory` |
| `assignment` | `AssignmentStrategy`, `CheapestVehicleStrategy`, `SmallestFitStrategy` |
| `service` | `DispatchService` (the core), `ReportService`, `DistanceProvider`, `DispatchSummary` |
| `repository` | `VehicleRepository`, `ParcelRepository` and their CSV versions |
| `event` | `DeliveryListener`, `CustomerNotifier`, `AuditLog` |
| `adapter` | `LegacyZoneService` (the old system) and `LegacyDistanceAdapter` |
| `factory` | `VehicleFactory` |
| `exception` | the custom exceptions (see below) |
| `utils`, `view` | file reading, CSV splitting, amount formatting, console display |

## Technical decisions

**Strategy for the price and for the vehicle choice.** Both are interfaces. Adding a pricing rule or a strategy means writing one class and `DispatchService` does not change. The demo switches from one strategy to the other at runtime between the two rounds.

**Two energy interfaces instead of a `refill()` method in `Vehicle`.** A van runs on fuel. A drone runs on a battery. A shared method would force one of them to implement something that makes no sense. So `Drone` and `CargoBike` are `Rechargeable` and `Van` is `Refuelable`. The service puts a vehicle back in shape without knowing its concrete type.

**Repositories that receive lines and not files.** `CsvFileReader` reads the file. `CsvVehicleRepository` interprets the lines. The tests need no file at all: they directly provide a `List<String>`.

**An Adapter for distances.** `LegacyZoneService` speaks in miles and returns -1 for an unknown zone. The adapter converts to kilometres and throws an `UnknownZoneException`. The rest of the code never sees the old format.

**No Singleton.** It hides dependencies and makes tests harder. Everything is built in one single place (`Application`) and then passed through constructors.

## Where inheritance is used

`Drone`, `Van` and `CargoBike` extend `Vehicle`. It is a real "is a" relationship: the maximum load, the range and the busy state are common. Only the cost per kilometre changes.

## Where polymorphism is used

The service handles `Vehicle` objects and calls `costPerKm()` without knowing which one it holds. Each subclass answers with its own cost.

A finer case: `Vehicle.canDeliver()` calls `acceptsHandling()`, which `Drone` overrides to refuse fragile parcels that are too heavy. The service contains no `if (vehicle instanceof Drone)`. There is one `instanceof` in `DispatchService` but it only decides between recharging and refuelling. It targets an interface and not a concrete class.

The pricing and assignment strategies follow the same principle. The service calls the interface and the real object decides the behaviour.

## Where the abstract class and the interfaces are used

`Vehicle` is abstract. A generic vehicle does not exist in the domain and the class carries a state (load, range, busy flag) shared by the subclasses. An interface cannot carry that state.

The interfaces describe contracts without state: `Rechargeable`, `Refuelable`, `PricingPolicy`, `AssignmentStrategy`, `DeliveryListener`, `DistanceProvider` and the two repositories. If I had to sum it up: abstract class when we share data. Interface when we share a capability.

## Custom exceptions

- `InvalidParcelException` is unchecked. A negative weight is a data error and the parcel is refused as soon as `build()` is called.
- `NoVehicleAvailableException` is checked. Finding no vehicle is a normal business case and the caller must decide what to do about it.
- `UnknownZoneException` is checked for the same reason. An unknown zone must not make the whole batch fail.
- `DataLoadException` is checked and keeps the original cause (`IOException` or `NumberFormatException`). Without its data the application cannot start. It stops with a clear message.

## Data loading

Three CSV files in `src/main/resources/` are read at startup. No vehicle or parcel is created by hand in the code.

| File | Columns |
|---|---|
| `vehicles.csv` | `type,id,maxLoadKg,rangeKm` where `type` is `DRONE`, `VAN` or `BIKE` |
| `parcels.csv` | `id,weightKg,zone,fragile,express,insuredValue` (booleans written `true` or `false`) |
| `zones.csv` | `zone,distanceMiles` |

Numeric values are integers. `CsvFileReader` reads the lines. `CsvParser` skips the header and splits the columns. Each repository validates and then builds the objects (`VehicleFactory` picks the class from the type). An invalid file produces a `DataLoadException` that points to the faulty line.

## How the SOLID principles are applied

**S (single responsibility).** Each class has one job. `CsvFileReader` reads. The repositories interpret. `ReportService` computes. `ConsoleView` displays. `Application` is limited to wiring and launching.

**O (open/closed).** A new pricing rule, a new strategy or a new subscriber is added by creating a class. No service is modified. The one compromise is the `switch` in `VehicleFactory`, which grows with every new vehicle type. I accepted it because it sits alone at the edge of the system and everything else only sees `Vehicle` objects.

**L (Liskov substitution).** Any `Vehicle` can replace another `Vehicle`. `Drone` restricts what it accepts but goes through an extension point designed for that (`acceptsHandling`). The service is never surprised.

**I (interface segregation).** The interfaces are small. `DistanceProvider` has a single method. `Rechargeable` and `Refuelable` are separate so that `Van` has nothing to do with a battery.

**D (dependency inversion).** `DispatchService` only knows abstractions: repository, distance provider, strategy and subscribers. Everything is injected through the constructor. The tests prove it because they use an in-memory fake repository and a fake distance provider.

## Running the tests

The tests use JUnit 5 and live in `src/test/java`. With Maven:

```bash
mvn test
```

In IntelliJ: right-click `src/test/java` then *Run 'All Tests'*. There are 146 tests. They cover normal cases and edge cases (a parcel exactly at the maximum load, a range that is exactly enough). They also cover errors with `assertThrows`. Several tests check that the state stays untouched after an exception: a failed dispatch must not mark any vehicle as busy.

`ApplicationFlowTest` reuses the wiring of `Application` with the real files. It checks the full demo scenario.

One practical detail to finish: package names must stay in lowercase. Windows ignores the case of folder names and Java does not.

## Limits

There is no database, no graphical interface and no user input. Everything comes from the files and the result is printed in the console.

Parcel `P-006` will never be delivered. No vehicle can carry 1,500 kg. I left it in the data on purpose to show a refusal.