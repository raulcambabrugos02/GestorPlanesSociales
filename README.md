# 📅 Gestor de Planes Sociales

A terminal-based Java application for managing social plans and activities. Users can register, create events, join plans, add activities, and rate their experiences — all from the command line.

Built as part of the Object-Oriented Programming course at **Universidad Politécnica de Madrid (UPM)**.

---

## Features

- **User management** — register, login and logout
- **Plan management** — create, edit and delete social plans with optional capacity limits
- **Activity management** — add activities to plans (Cinema, Theatre, Generic) with type-specific discounts
- **Subscriptions** — join or leave plans, view subscribed plans
- **Ratings** — rate a plan after attending it; score is calculated as a running average
- **Search & filter** — find plans by date, search events with a specific friend, filter activities by cost and capacity
- **Cost calculation** — compute the total cost of a plan per user, applying age-based discounts automatically

---

## Architecture

The project follows an **MVC (Model-View-Controller)** pattern:

```
src/
└── es/upm/etsisi/iwsim21/grupo5/poo/
    ├── modelo/               # Domain classes
    │   ├── Actividad.java    # Base activity class
    │   ├── Cine.java         # Cinema — 50% discount for users ≤21
    │   ├── Teatro.java       # Theatre — discounts for youth (≤25) and seniors (≥65)
    │   ├── Plan.java         # Social plan with participants and activities
    │   └── Usuario.java      # User with subscribed plans map
    ├── controlador/          # Business logic
    │   ├── CActividad.java   # Activity controller
    │   ├── CPlan.java        # Plan controller
    │   └── CUsuario.java     # User controller
    ├── vista/
    │   └── VistaCLI.java     # Command-line interface
    └── GPlanesSociales.java  # Entry point
```

---

## OOP Concepts Applied

| Concept | Where |
|---|---|
| **Inheritance** | `Cine` and `Teatro` extend `Actividad` |
| **Polymorphism** | `getDescuento(Usuario)` overridden in each activity subtype |
| **Encapsulation** | Private fields with getters across all model classes |
| **Custom exceptions** | Dedicated exception hierarchy for users, plans and activities |
| **Collections** | `LinkedList`, `HashMap` for plans, participants and ratings |

---

## Getting Started

### Prerequisites

- Java 17 or higher
- A terminal

### Run

```bash
# Compile
javac -d out src/es/upm/etsisi/iwsim21/grupo5/poo/**/*.java

# Run
java -cp out es.upm.etsisi.iwsim21.grupo5.poo.GPlanesSociales
```

---

## Available Commands

```
create-user:<<username>>;<<age>>;<<phone>>;<<password>>
login:<<username>>;<<password>>
logout:
create-activity:<<type>>;<<name>>;<<description>>;<<duration>>;<<cost>>;<<capacity (optional)>>
create-event:<<name>>;<<date yyyy-MM-ddTHH:mm>>;<<location>>;<<capacity (optional)>>
delete-event:<<planName>>
add-activity-plan:<<planId>>;<<activityId>>
list-events:fecha|puntuacion
list-events-subscribed:
join-event:<<planId>>
exit-event:<<planId>>
rate:<<score>>;<<planId>>
cost-event-subscribed:<<planName>>
search-activity-coste-aforo:<<cost>>;<<capacity>>
search-events-with-friend:<<name>>
search-events-before-date:<<date yyyy-MM-ddTHH:mm>>
```

### Activity types

| Type | Discount |
|---|---|
| `Generic` | No discount |
| `Cinema` | 50% for users aged 21 or under |
| `Theatre` | 50% for users aged 25 or under · 70% for users aged 65 or over |

---

## Example Usage

```
create-user:raul;22;600000000;1234
login:raul;1234
create-event:FiestaCampus;2026-12-01T18:00;Madrid;50
create-activity:Cinema;PeliculaX;Thriller de accion;120;8.0;100
add-activity-plan:1;1
join-event:1
```

---

## Author

**Raúl Camba Brugos** — Information Systems Engineering @ UPM  
[LinkedIn](https://linkedin.com/in/raúl-camba-brugos-079667273) · raulcambabrugos.dev@gmail.com
