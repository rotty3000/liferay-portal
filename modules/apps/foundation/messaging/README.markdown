# Messaging

Liferay provides an easy-to-use messaging utility called the Message Bus. The
Message Bus provides a flexible API that allows application components to
create, send, and receive messages. Liferay makes extensive use of the Message
Bus for communication within and between Liferay applications. Liferay's
messaging utility is similar to Java's JMS but is lighter-weight and provides
a smaller and simpler API.

In previous versions of Liferay, the Message Bus was embedded in Liferay's
core. It has now been completely modularized and decoupled. This means it can now be
used as a standalone messaging utility.

The Message Bus consists of four OSGi modules:

- `messaging-api`: The `messaging-api` module provides an API that's intended
for use by Message Bus clients.
- `messaging-spi`: The `messaging-spi` module provides an SPI (service provider
interface) that's intended for use by Message Bus implementers (a.k.a.
Message Bus providers).
- `messaging-impl`: The `messaging-impl` module uses the `messaging-spi` module
to provide a complete implementation of all the services required to satisfy
the contracts promised by the `messaging-api` module.
- `messaging-test`: The `messaging-test` module contains integration tests that
launch an OSGi runtime, install the `messaging-api`, `messaging-spi`, and
`messaging-impl` modules, exercise every method of each class in the
`messaging-api` module, and compare the expected results to the actual
results.

Next, let's look at the basic concepts and architecture of the Message Bus
system.

## Concepts

To use the Message Bus, you should understand these Message Bus concepts.

*Note:* The term 'Message Bus' can be used either as (1) a general term for Liferay's
messaging utility or (2) as a specific software component in Liferay's
messaging utility. Below, we use the term in the second sense.

- **Message Bus:** Manages the sending of messages and the destinations to which
they are sent
- **Destination:** Defines an endpoint to which messages can be sent and message
listeners can subscribe. There are three main types of destinations which
correspond to their supported messaging types, described below.
	- ParallelDestination
	- SerialDestination
	- SynchronousDestination
- **Message Listener:** Defines a message consumer which subscribes to destinations
and receives messages
- **Asynchronous messaging:** In this form of messaging, the sender sends a message
	to a destination and continues processing without waiting for any responses.
	Responses can optionally be sent (depending on whether or not the message
	includes response destination information) but are not required. Messages
	sent to a destination are delivered to the destination's registered message
	listeners in separate worker threads. This frees the sending thread to
	continue processing without delay. There are two kinds of asynchronous
	messaging:
	- **Parallel messaging:** In this form of messaging, one worker thread is
		created for each message for each message listener. Thus, messages are
		delivered to the message listeners in parallel.
	- **Serial messaging:** In this form a messaging, one worker thread is simply
		created for each message. Thus, messages are delivered to the message
		listeners one at a time.
- **Synchronous messaging:** In this form of messaging, the sender sends a message
	to a destination and waits for a response. This form of messaging expects a
	response.

In summary, the Message Bus is responsible for managing a list of destinations.
Destinations are messaging endpoints which each support a particular kind of
messaging. Destinations each manage a list of message listeners which are
responsible for specifying the processing that should take place when a message
is received.

## Usage

Liferay's Message Bus modules are designed to run in an OSGi runtime. To use
the Message Bus, you must install the `messaging-api`, `messaging-impl`, and
`messaging-spi` modules and all of their dependencies into your application's
OSGi runtime. The
[OSGi EnRoute tutorials](http://enroute.osgi.org/book/150-tutorials.html)
provide an excellent introduction to OSGi development and they explain how to
use [bnd](http://bnd.bndtools.org/) to make dependency management and
resolution quite easy.
<!-- TODO: Add link to public repository where the Liferay messaging modules
	and their dependencies can be found. -->

The `messaging-impl` module includes many OSGi services. When writing a message bus
client, you'll use these services to create, send, and receive message. You need not
concern yourself with *how* these services do their job, you just need to know
*what* functionality these services provide and how they can help you design your
application. In other words, to use the Message Bus, you should learn the API
of the `messaging-api` module and need to not worry about understanding the
other messaging modules.

### Example: Sending a Message

Let's explore the Message Bus API by looking at some typical examples of basic
usage. Suppose you want to create and send a message from one component of your
application to another. You can accomplish this in three easy steps:

1. Create a destination and register it with the message bus.

2. Create a message listener and register it with the destination.

3. Create, populate, and send a message to the destination registered in step 1.

Let's look at these steps in detail.

#### Step 1: Creating and Registering a Destination

Users should create Destinations indirectly, via DestinationConfigurations.
This way, users aren't exposed to destination implementation details. The
`messaging-api` module provides the base class
`com.liferay.messaging.DestinationConfiguration` for creating destination
configurations.

To create a parallel destination with the name "parallelDestination", do
this:

```.java
@Component(service = DestinationConfiguration.class)
public class MySynchronousDestination extends DestinationConfiguration {

	public MySynchronousDestination() {
		super(DestinationType.PARALLEL, "parallelDestination");
	}

}
```

#### Step 2: Creating and Registering a MessageListener

Now that your destination has been created and registered, it's time to create
a message listener and register it with that destination. Here's one way to
create a message listener that targets the "parallelDestination":

```.java
@Component(property = "destination.name=parallelDestination")
public class MyMessageListener implements MessageListener {

	@Override
	public void receive(Message message) throws MessageListenerException {
		// Your processing here
		System.out.println("Received message: " + message);
	}

}
```

Since your class is decorated with the
`org.osgi.service.component.annotations.Component` annotation and
the class directly implements the `MessageListener` interface, your class is
automatically registered as a message listener when your bundle starts. Notice
that the destination name is specified as "parallelDestination".

#### Step 3: Creating, Populating, and Sending Messages

To create and populate a message, simply create a new `Message` instance. The
information contained by a message is called its payload. The payload is a
generic object so you can make the payload anything you want. Messages can also
contain an arbitrary number of additional name / value pairs. Here's an example:

```.java
Message message = new Message();

message.setPayload("payload");
```

If you want to specify some additional name / value pairs, you can do it like
this:

```.java
message.put("property1", "value1");
message.put("property2", "value2");
```

Instead of assigning individual name / value pairs, you can replace the entire
map with your own map like this:

```.java
Map<String, Object> messageMap = new HashMap<>();

message.setValues(messageMap);
```

Once your message is populated, you can send it via the message bus. The
`messaging-impl` module publishes a message bus instance as a service. Let's
create a messaging component again using Declarative Services which depends on
the MessageBus service:

```.java
@Component(immediate = true)
class MessagingComponent {

	@Reference
	private MessageBus _messageBus;

}
```

Once you've obtained a reference to the message bus, you can send the message
like this:

```.java
_messageBus.sendMessage("parallelDestination", message);
```

Although creating and sending messages this way is easy enough, it's even
easier to create and send messages using a message builder. Message builders
are created from message builder factories. A message builder factory service
is provided by the `messaging-impl` module. Here's an example of how to obtain
a message builder:

```.java
@Component(immediate = true)
class MessagingBuilderComponent {

	@Reference
	private MessageBuilderFactory _messageBuilderFactory;

}
```

After you've obtained a reference to the message builder factory, you can
construct a message builder like this:

```.java
MessageBuilder messageBuilder = _messageBuilderFactory.create(
	"parallelDestination");
```

Notice that you have to supply a destination name when creating a message
builder. Using the message builder to configure a message is easy. You can use
chaining like this:

```.java
messageBuilder.setPayload(
	"payload2"
).put(
	"property3", "value3"
).put(
	"property4", "value4"
);
```

If needed, you could use the message builder to obtain an instance of the
configured message like this:

```.java
Message message = messageBuilder.build();
```

A message obtained like this is already configured with the destination of its
message builder. However, you can send a message from the message builder
directly without first obtaining a message instance:

```.java
messageBuilder.send();
```

This method invocation has the same effect as invoking
`messageBus.sendMessage(...)` with the destination and message configured in
the message builder.

A complete example looks like this:

```.java
@Component(immediate = true)
class MessagingBuilderComponent {

	public void businessMethod(String payload, int foo, long bar) {
		_messageBuilderFactory.create(
			"parallelDestination"
		).setPayload(
			payload
		).put(
			"property3", foo
		).put(
			"property4", bar
		).send();
	}

	@Reference
	private MessageBuilderFactory _messageBuilderFactory;

}
```

### Example: Sending a Message and Receiving a Response

Sometimes, it's important for application components to be able to send
messages back and forth or at least to be able to send some kind of
acknowledgement message to indicate that a message was received.

The Message Bus's synchronous messaging functionality supports this use case.
The steps for setting up synchronous messaging are similar to those for setting
up asynchronous messaging:

1. Create a destination and register it with the message bus.

2. Create a message listener and register it with the destination. The message
listener is responsible for all message processing. This means that if a
response is required, the message listener is responsible for creating and
sending it.

3. Create, populate, and send a synchronous message to the destination
registered in step 1.

Here's a simple example of synchronous messaging in action:

#### Step 1: Creating and Registering a Destination

This step is the same as for asynchronous messaging.

```.java
@Component(service = DestinationConfiguration.class)
public class MySynchronousDestination extends DestinationConfiguration {

	public MySynchronousDestination() {
		super(DestinationType.SYNCHRONOUS, "synchronousDestination");
	}

}
```

Note that there's a difference between a synchronous message and a synchronous
destination. A synchronous message is a message that expects a response.
Messages sent to synchronous destinations are sent on the sender's thread while
messages sent to asynchronous destinations are sent on separate worker threads.
So it's perfectly acceptable to send a synchronous message to a parallel
(asynchronous) destination.

#### Step 2: Creating and Registering a MessageListener

Next, you need to create a message listener and register it with that
destination. Your message listener is responsible for creating and a sending a
response. Here's a simple way to create and register a message listener that
does this:

```.java
@Component(property = "destination.name=parallelDestination")
public class ResponseMessageListener implements MessageListener {

	@Override
	public void receive(Message message) throws MessageListenerException {
		// Simply bounce the message back to the response destination
		_messageBuilderFactory.createResponse(
			message
		).setPayload(
			message
		).send();
	}

	@Reference
	private MessageBuilderFactory _messageBuilderFactory;

}
```

The message listener defined above uses the message builder factory service to
create a response message builder. When creating a message or message builder,
you can supply a response destination name. If omitted, the destination name
defaults to `DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE` =
`"liferay/message_bus/default_response"`. This is the destination to which the
response constructed by the message listener above is sent.

The response message's payload is set to the received message so that the
sender can check the payload to ensure that the receiver received the intended
message. The response is sent asynchronously (`responseMessageBuilder.send()`)
since only the original message requires a response. The response does not
itself require a response.

#### Step 3: Creating, Populating, and Sending Synchronous Messages

Creating a synchronous message is done exactly the same way as shown earlier. You
can either create a new message directly or you can create a message builder instead.

Here's the direct method:

```.java
Message message = new Message();

message.setPayload("payload");
message.setResponseDestinationName(DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
```

To send the message, use the message bus like this:

```.java
Object response = _messageBus.sendSynchronousMessage(destinationName, message);
```

Note that `sendSynchronousMessage` returns a response object. In this example, our
message listener defined in step 2 set the response message payload to the original
message so we expect the response to be a `Message`. However, remember that in
general, a message response, like a message payload, can be any object. If you want
to visually check that the message response equals the original message, print the
result of `sendSynchronousMessage`:

```.java
System.out.println("response: " + response);
```

Instead of creating a message manually and then sending it via the Message Bus,
you can use a message builder:

```.java
Object response = _messageBuilderFactory.create(
	destinationName
).setPayload(
	"payload"
).sendSynchronous();
```

Here, the `_messageBuilderFactory` can be obtained the same way as shown in step 2.

Of course, you can check the response as well:

```.java
System.out.println("response: " + response);
```

Now that you've learned how to use the message bus to send and receive messages
and response messages, it's time to explore some other features of the message
bus.

### Example: Message Bus Event Listeners and Destination Event Listeners

The messaging API provides a number of event-based extension points which
application developers can use to specify additional processing that should
take place. In this section, you'll see how to specify processing that should
take place when destinations are added to or removed from the message bus or
when message listeners are added to or removed from a destination. In the next
section, you'll see how to specify processing that should take place
immediately before or after a message is sent or received.

To specify processing that should take place when destinations are added to or
removed from the message bus, simply create a message bus event listener and
register it as an OSGi service:

```.java
@Component
class MyMessageBusEventListener implements MessageBusEventListener {

	@Override
	public void destinationAdded(Destination destination) {
		// Your processing here
		System.out.println("Destination added!");
	}

	@Override
	public void destinationRemoved(Destination destination) {
		// Your processing here
		System.out.println("Destination removed!");
	}

}
```

After registering your message bus event listener service, your
`destinationAdded` and `destinationRemoved` methods will be invoked whenever a
destination is added or removed. The `destinationAdded` operation will be
retroactively called for every existing Destination.

Destination event listeners work nearly the same way:

```.java
@Component(property = "destination.name=some_destination")
class MyDestinationEventListener implements DestinationEventListener {

	@Override
	public void messageListenerRegistered(
		String destinationName, MessageListener messageListener) {
		// Your processing here
		System.out.println(
			"Message listener registered with " + destinationName + "!");
	}

	@Override
	public void messageListenerUnregistered(
		String destinationName, MessageListener messageListener) {
		// Your processing here
		System.out.println(
			"Message listener unregistered with " + destinationName + "!");
	}

}
```

One important difference is that while message bus event listeners listen
globally (at the Message Bus scope) for any destinations that are added or
removed, destination event listeners only listen for message listeners that are
added to or removed from a particular destination. So you must specify the
destination as a property of your destination event listener service.

Don't confuse these three types of listeners:

- `MessageBusEventListener`: Specifies processing to take place when any
destinations are added to or removed from the message bus.
- `DestinationEventListener`: Specifies processing to take place when any
message listeners are added to or removed from a specific destination.
- `MessageListener`: Specifies processing to take place when a message is
received by a specific destination.

### Example: Inbound and Outbound Message Processors

Outbound message processors specify message processing that should take place
immediately before or after a message is sent. Inbound message processors
specify message processing that should take place immediately before or after a
message is received. The message that's actually sent or received may be
altered by this processing.

Inbound and outbound message processors provide a great deal of flexibility for
application developers. Suppose, for example, that your application sends
certain kinds of messages. The messages are required to carry certain kinds of
payloads or certain key / value pairs. As your application evolves, what if you
need the message format to change based on a condition unknown to the message
sender? It might be possible "fix up" messages immediately before they're sent
or immediately before they're received to make sure they satisfy the condition.
Although the condition might be unknown to the message sender, you could write
outbound and / or inbound message processors to check the condition and "fix
up" the sent or received messages as required.

Note that inbound and outbound message processor factories are registered with
destinations, not the processors themselves. When a message is sent or
received, each registered outbound message processor factory is used to create
a new message processor instance. The functions of each message processor
instance are invoked for the corresponding stages of the sending process:

- Immediately before a message is sent:
	`OutboundMessageProcessor.beforeSend(Message)`
- Immediately after a message is sent:
	`OutboundMessageProcessor.afterSend(Message)`
- Immediately before a message is queued for processing:
	`InboundMessageProcessor.beforeReceive(Message)`
- Immediately after a message has been queued for processing:
	`InboundMessageProcessor.afterReceive(Message)`
- Immediately before a message is processed by the receiving thread:
	`InboundMessageProcessor.beforeThread(Message, Thread)`
- Immediately after a message is processed by the receiving thread:
	`InboundMessageProcessor.afterThread(Message, Thread)`

Here's an example of how to create an outbound message processor:

```.java
@Component(property = "destination.name=some_destination")
class MyOutboundMessageProcessorFactory
	implements OutboundMessageProcessorFactory {

	@Override
	public OutboundMessageProcessor create() {
		return new OutboundMessageProcessor() {
			@Override
			public void afterSend(Message message)
				throws MessageProcessorException {

				// Your processing here
				System.out.println("In afterSend!");
			}

			@Override
			public Message beforeSend(Message message)
				throws MessageProcessorException {

				// Your processing here
				System.out.println("In beforeSend!");

				message.put("extraKey", "extraValue");

				return message;
			}
		}
	}

}
```

In this example, since the outbound message processor is stateless, it's silly
to return a new outbound message processor instance for each invocation of
`OutboundMessageProcessorFactory.create()`. However, for nontrivial use cases,
it's usually important for message processor factories to return new message
processor instances (so that programming models such as "around advice" can be
implemented). Notice that the message processor factory must be registered to a
specific destination. This is done using the pattern you've seen several times
already.

Now if you send a message to the destination to which you registered your
message processor, you'll see that the `beforeSend` and `afterSend` methods
were invoked:

```.java
_messageBuilderFactory.create(
	destinationName
).setPayload(
	"messagePayload"
).send();
```

You can also check that the extra key / value pair added to the message in the
`beforeSend` method appears in the received message.

Inbound message processors work similarly to outbound message processors.

### Message Bus Configuration

The message bus provides two configuration options:

- `synchronous-message-sender-mode`
- `synchronous-message-sender-timeout`

`synchronousMessageSenderMode` has two possible values: `DEFAULT` or `DIRECT`.
When in `DEFAULT` mode, the message bus uses a message sender that invokes the
destination's `send` method. This means that the destination is responsible for
determining how the messages are delivered to the destination's message
listeners. If the destination is a parallel destination, for example, messages
will be dispatched to the message listeners on parallel worker threads. When in
`DIRECT` mode, the message bus uses a message sender that directly invokes the
`receive` method of each of the destination's message listeners. There is not
even a possibility for separate worker threads to be involved since the
destination type is ignored.

`synchronous-message-sender-timeout` represents the amount of time, in
milliseconds, to wait for a response when a synchronous message is sent. If no
response is received before the time expires, a `MessageBusException` is
thrown.

Both the `synchronousMessageSenderMode` and the
`synchronous-message-sender-timeout` properties can be configured via standard
OSGi Configuration Admin tools. You can configure these properties, for example, using
the user interface provided by
[Apache Felix Web Console](http://felix.apache.org/documentation/subprojects/apache-felix-web-console.html).
Or you could use a configuration properties file with
[Apache Felix File Install](http://felix.apache.org/documentation/subprojects/apache-felix-file-install.html).

### Destination Configuration

Destinations also provides some configuration options:

- `destination-name`
- `max-queue-size`
- `worker-core-size`
- `worker-max-size`

The `destination-name` indicates the specific destination to which the worker
configuration specified by the remaining three configuration options applies.

Recall that when messages are sent to an asynchronous (parallel or serial)
destination, they are added to a queue before they are dispatched on worker
threads. The `max-queue-size` property determines the maximum size of this
queue. If there are more messages to be queued up for dispatch than the queue
can hold, the excess messages are not sent. These cases can optionally be
handled by a rejected execution handler. See the next section for details.

The `worker-core-size` and `worker-max-size` properties control the size of the
destination's thread pool. The core and maximum pool sizes work the same way as
for Java's `ThreadPoolExecutor` class: [http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html](http://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html).

As with the message bus configuration options, destination configurations can
be configured via standard OSGi Configuration Admin tools.

### Rejected Execution Handlers

Rejected execution handlers determine how to handle the failure that occurs
when the number of messages to be sent exceeds the maximum thread pool size. To
create a rejected execution handler, implement the `RejectedExecutionHandler`
interface and publish your class as an OSGi service. As seen earlier, this can
be easily done by creating your class as a declarative services component.
Here's an example:

```.java
	@Component(
		property = {"destination.name=yourDestinationName"},
		service = {RejectedExecutionHandler.class}
	)
	public class RejectedExecutionHandlerImpl
		implements RejectedExecutionHandler {

		@Override
		public void rejectedExecution(
			Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {

			_map.put((MessageRunnable)runnable, threadPoolExecutor);
		}

		private final Map<MessageRunnable, ThreadPoolExecutor> _map =
			new ConcurrentHashMap<>();

	}
```

<!-- TODO: Is there a way to make rejected execution handlers apply to all
destinations? -->
Rejected execution handlers are applied to specific destinations so remember to
specify the appropriate destination name in your handler's `@Component`
annotation.

### Executor Service Registrars

Executor Service Registrars provide a mechanism for tracking thread pools. If
you need to track the thread pools of your destinations, publish executor
service registrars for your destinations. To do so, implement the
`ExecutorServiceRegistrar` interface and create your class as a declarative
services component. Here's an example:

```.java
@Component(
	property = {"destination.name=yourDestinationName"},
	service = {ExecutorServiceRegistrar.class}
)
public class ExecutorServiceRegistrarImpl
	implements Callable<Map<String, ExecutorService>>,
			   ExecutorServiceRegistrar {

	@Override
	public <T extends ExecutorService> T registerExecutorService(
		String name, T executorService) {

		_executorServices.put(name, executorService);

		return executorService;
	}

	private final Map<String, ExecutorService> _executorServices =
		new ConcurrentHashMap<>();

}
```

<!-- TODO: Is there a way to make rejected execution handlers apply to all
destinations? -->
Executor service registrars, like rejected execution handlers, are applied to
specific destinations so remember to specify the appropriate destination name
in your handler's `@Component` annotation.
