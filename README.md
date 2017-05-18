# Indicators

A pop-up over an entity's head that can be used to give some information about the entity. For eg, when used on an AI, whether it is puzzled, hostile, sleeping etc.


## Available Indicators

1. Exclamation
2. Question
3. Knocked Out

## Usage

There are two ways to add an indicator.

1. Simply add the indicator component to an entity specifying the indicator uri. However in this approach you *must* check if an indicator component is already present and if so remove the old one.

```java
if(entity.hasComponent(IndicatorComponent.class))
    entity.removeComponent(IndicatorComponent.class);
entity.addComponent(new IndicatorComponent("Indicators:knockedOutIndicator"));
```

2. Send a DisplayIndicatorEvent which performs the check for you.

```java
entity.send(new DisplayIndicatorEvent(new IndicatorComponent("Indicators:knockedOutIndicator")));
```

To remove an indicator, simply remove the component.

```java
entity.removeComponent(IndicatorComponent.class);
```
