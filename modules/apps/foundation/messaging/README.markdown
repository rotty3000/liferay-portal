# Messaging

Liferay provides an easy-to-use messaging utility called the Message Bus. The
Message Bus provides a flexible API that allows application components to
create, send, and receive messages. Liferay makes extensive use of the Message
Bus for communication within and between Liferay applications. Liferay's
messaging utility is similar to Java's JMS but is lighter-weight and provides
a smaller and simpler API.

In previous versions of Liferay, the Message Bus was embedded in Liferay's
core. It has now been completely modularized and decoupled from Liferay's core.
This means it can now be used as a standalone messaging utility.

The Message Bus consists of four OSGi modules:

- `messaging-api`: The `messaging-api` module provides an API that's intended
  for use by Message Bus clients. 
- `messaging-impl`: The `messaging-impl` module uses the `messaging-spi` module
  to provide a complete implementation of all the services required to satisfy
the contracts promised by the `messaging-api` module. 
- `messaging-spi`: The `messaging-spi` module provides an SPI (service provider
  interface) that's intended for use by Message Bus implementers (a.k.a.
Message Bus providers). 
- `messaging-test`: The `messaging-test` module contains integration tests that
  launch an OSGi runtime, install the `messaging-api`, `messaging-spi`, and
`messaging-impl` modules, exercise every method of each class in the
`messaging-api` module, and compare the expected results to the actual results.

Next, let's look at the basic concepts and architecture of the Message Bus
system.

## Concepts

To use the Message Bus, you should understand these Message Bus concepts. Note:
The term 'Message Bus' can be used either as (1) a general term for Liferay's
messaging utility or (2) as a specific software component in Liferay's
messaging utility. Below, we use the term in the second sense.

- Message Bus: Manages the sending of messages and the destinations to which
  they are sent
- Destination: Defines an endpoint to which messages can be sent and message
  listeners can subscribe. There are three main types of destinations which
correspond to their supported messaging types, described below.
	- ParallelDestination
	- SerialDestination
	- SynchronousDestination
- Message Listener: Defines a message consumer which subscribes to destinations and receives messages
- Asynchronous messaging: In this form of messaging, the sender sends a message
  to a destination and continues processing without waiting for any responses.
Responses can optionally be sent (depending on whether or not the message
includes response destination information) but are not required. Messages sent
to a destination are delivered to the destination's registered message
listeners in separate worker threads. This frees the sending thread to continue
processing without delay. There are two kinds of asynchronous messaging:
	- Parallel messaging: In this form of messaging, one worker thread is
	  created for each message for each message listener. Thus, messages are
delivered to the message listeners in parallel.
	- Serial messaging: In this form a messaging, one worker thread is simply
	  created for each message. Thus, messages are delivered to the message
listeners one at a time.
- Synchronous messaging: In this form of messaging, the sender sends a message
  to a destination and waits for a response. This form of messaging expects a
response. The sending thread sends each message to each message listener by
itself; no worker threads are created.

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
<!-- TODO: Add link to public repository where the Liferay messaging modules and their dependencies can be found. -->

The `messaging-impl` module includes many declarative services components that
register OSGi services. When writing a message bus client, you'll use these
services to create, send, and receive message. You need not concern yourself
with *how* these services to their job, you just need to know *what*
functionality these services provide and how they can help you design your
application. In other words, to use the Message Bus, you should learn the API
of the `messaging-api` module and need to not worry about understanding the
other messaging modules.

### Example: Sending a Message

Let's explore the Message Bus API by looking at some typical examples of basic
usage. Suppose you want to create and send a message from one component of your
application to another. You can accomplish this in three easy steps:

1. Create a destination and register it with the message bus.

2. Create a message listener and register it with the destination.

3. Create, populate, and send a message to the destination registered in step
   1.

Let's look at these steps in detail.

#### Step 1: Creating and Registering a Destination

Users should create Destinations indirectly, via DestinationConfigurations.
This way, users aren't exposed to destination implementation details. The
`messaging-api` module provides three static methods in the
`DestinationConfiguration` class for creating destination configurations:

- `createParallelDestinationConfiguration(String destinationName)`
- `createSerialDestinationConfiguration(String destinationName)`
- `createSynchronousDestinationConfiguration(String destinationName)`

Thus, to create a parallel destination with the name "parallelDestination", do
this:

	DestinationConfiguration parallelDestinationConfiguration =
		DestinationConfiguration.createParallelDestinationConfiguration(
		"parallelDestination"); 

You must register your DestinationConfiguration as an OSGi service in order for
your destination to be made available:

	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
	BundleContext bundleContext = bundle.getBundleContext();
	bundleContext.registerService(DestinationConfiguration.class,
		parallelDestinationConfiguration, null);

Note: FrameworkUtil, Bundle, and BundleContext belong to the standard OSGi API.

#### Step 2: Creating and Registering a MessageListener

Now that your destination has been created and registered, it's time to create
a message listener and register it with that destination. Here's one way to
create a message listener:

	MessageListener messageListener = new MessageListener() {
		@Override
		public void receive(Message message) throws MessageListenerException {
			// Your message processing instructions go here
			System.out.println("Received message: " + message);
		}
	};

You can register this listener similarly to how you registered your
DestinationConfiguration:

	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
	BundleContext bundleContext = bundle.getBundleContext();
	Dictionary<String, Object> properties = new Hashtable<String, Object>();
	properties.put("destination.name", "parallelDestination");
	bundleContext.registerService(MessageListener.class, messageListener, properties);
	
The main difference here is that you need to associate your message listener
with the destination that you created, namely, the "parallelDestination"
destination. This is done by passing a dictionary containing the association as
a third parameter when using your application's bundle context to register your
message listener service.

An alternative way to create an register a message listener is to create your
MessageListener class as a declarative services component. Here's how to do
this:

	@Component(
		property = "destination.name=parallelDestination",
		scope = ServiceScope.SINGLETON,
		service = {MessageListener.class}
	)
	public class MyMessageListener implements MessageListener {
		@Override
		public void receive(Message message) throws MessageListenerException {
			// Your message processing instructions go here
			System.out.println("Received message: " + message);
		}
	}

Since your class is decorated with the
`org.osgi.service.component.annotations.Component` annotation and
`MessageListener.class` is specified as the service, your class is
automatically registered as a message listener when your bundle starts. Notice
that the destination name must also be specified as "parallelDestination" as in
the first method of registering the service.

#### Step 3: Creating, Populating, and Sending Messages

To create and populate a message, simply create a new `Message` instance. The
information contained by a message is called its payload. The payload is a
generic object so you can make the payload anything you want. Messages can also
contain an arbitrary number of additional name / value pairs. Here's an example:

	Message message = new Message();
	message.setPayload("payload");

If you want to specify some additional name / value pairs, you can do it like
this:

	message.put("property1", "value1");
	message.put("property2", "value2");

Instead of assigning individual name / value pairs, you can replace the entire
map with your own map like this: 

	Map<String, Object> messageMap = new Hashtable<>();
	messageMap.put("property1", "value1");
	messageMap.put("property2", "value2");
	message.setValues(messageMap);

Once your message is populated, you can send it via the message bus. The
`messaging-impl` module publishes a message bus instance as a service. You can
obtain a reference to it like this:

	MessageBus messageBus = null;
 
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
	BundleContext bundleContext = bundle.getBundleContext();

	ServiceTracker<MessageBus, MessageBus> messageBusTracker =
		new ServiceTracker<>(bundleContext, MessageBus.class, null);

	try {
		messageBusTracker.open();
			
		messageBus = messageBusTracker.waitForService(1000);

		if (messageBus == null) {
			throw new RuntimeException();
		}
	}
	catch (InterruptedException ie) {
		throw new RuntimeException(ie);
	}

Once you've obtained a reference to the message bus, you can send the message like this:

	messageBus.sendMessage("parallelDestination", message);

Remember that an OSGi service can disappear at any time. So don't hold on to
your message bus reference to reuse it later. Instead, use your service tracker
to obtain a fresh reference when needed later.

Although creating and sending messages this way is easy enough, it's even
easier to create and send messages using a message builder. Message builders
are created from message builder factories. A message builder factory service
is provided by the `messaging-impl` module. Here's an example of how to obtain
a message builder and how to use it to create a message:

	MessageBuilderFactory messageBuilderFactory = null;
 
	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
	BundleContext bundleContext = bundle.getBundleContext();

	ServiceTracker<MessageBuilderFactoryTracker, MessageBuilderFactoryTracker>
	messageBuilderFactoryTracker = new ServiceTracker<>(
		bundleContext, MessageBuilderFactoryTracker.class, null);

	try {
		messageBuilderFactoryTracker.open();
			
		messageBuilderFactory = messageBuilderFactoryTracker.waitForService(1000);

		if (messageBuilderFactory == null) {
			throw new RuntimeException();
		}
	}
	catch (InterruptedException ie) {
		throw new RuntimeException(ie);
	}

After you've obtained a reference to the message builder factory, you can
construct a message builder like this:

	MessageBuilder messageBuilder =
		messageBuilderFactory.create("parallelDestination");

Notice that you have to supply a destination name when creating a message
builder. Using the message builder to configure a message is easy:

	messageBuilder.setPayload("payload2");
	
	messageBuilder.put("property3", "value3");
	messageBuilder.put("property4", "value4");

You could use the message builder to obtain an instance of the configured message like this:

	Message message = messageBuilder.build();

A message obtained like this is already configured with the destination of its
message builder. However, you can send a message from the message builder
directly without first obtaining a message instance:

	messageBuilder.send();

This method invocation has the same effect as invoking
`messageBus.sendMessage(...)` with the destination and message configured in
the message builder.

### Example: Sending a Message and Receiving a Response

Sometimes, it's important for application components to be able to send
messages back and forth or at least to be able to send some kind of
acknowledgement message to indicate that a message was received.

The message bus's synchronous messaging functionality supports this use case.
The steps for setting up synchronous messaging are similar to those for setting
up asynchronous messaging:

1. Create a destination and register it with the message bus.

2. Create a message listener and register it with the destination. The message
   listener is responsible for all message processing. This means that if a
response is required, the message listener is responsible for creating and
sending it.

3. Create a second message listener to listen for and process the response.
   Register it with the response destination.

4. Create, populate, and send a synchronous message to the destination
   registered in step 1.

Here's a simple example of synchronous messaging in action:

#### Step 1: Creating and Registering a Destination

This step is the same as for asynchronous messaging.

	DestinationConfiguration parallelDestinationConfiguration =
		DestinationConfiguration.createParallelDestinationConfiguration(
		"parallelDestination"); 

	Bundle bundle = FrameworkUtil.getBundle(this.getClass());
	BundleContext bundleContext = bundle.getBundleContext();
	bundleContext.registerService(DestinationConfiguration.class,
		parallelDestinationConfiguration, null);

<!-- TODO: Check for accuracy. -->
Note that there's a difference between a synchronous message and a synchronous
destination. A synchronous message is a message that expects a response.
Messages sent to synchronous destinations are sent on the sender's thread while
messages sent to asynchronous destinations are sent on separate worker threads.
So it's perfectly acceptable to send a synchronous message to a parallel
(asynchronous) destination.

<!-- TODO: Is there a difference in the meaning of the word 'synchronous'
between 'synchronousDestination` and `sendSynchronousMessage`? E.g., can either
synchronous or asynchronous messages be sent to a synchronous destination? In
addition to the definition of synchronous described above in the Concepts
section is there another definition of synchronous that means a message that
expects a response?-->

#### Step 2: Creating and Registering a MessageListener

Next, you need to create a message listener and register it with that
destination. Your message listener is responsible for creating and a sending a
response. Here's a simple way to create and register a message listener that
does this:

	MessageListener messageListener = new MessageListener() {
		@Override
		public void receive(Message message) throws MessageListenerException {
			MessageBuilder responseMessageBuilder = getMessageBuilderFactory().createResponse(message);
			
			responseMessageBuilder.setPayload(message);
			
			responseMessageBuilder.send();
		}
	};
	
	Dictionary<String, Object> properties = new Hashtable<String, Object>();
	properties.put("destination.name", "parallelDestination");

	_bundleContext.registerService(MessageListener.class, messageListener, properties);

This message listener uses a `getMessageBuilderFactory` helper function which
is defined like this:

	private MessageBuilderFactory getMessageBuilderFactory() {
		try {
			_messageBuilderFactoryTracker.open();
				
			MessageBuilderFactory messageBuilderFactory =
				_messageBuilderFactoryTracker.waitForService(_timeout);

			if (messageBuilderFactory == null) {
				throw new RuntimeException();
			}

			return messageBuilderFactory;
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}
	
In this function, `_messageBuilderTracker` is a private member variable which
was initialized like this:

	private ServiceTracker<MessageBuilderFactory, MessageBuilderFactory>
		_messageBuilderFactoryTracker;
	_messageBuilderFactoryTracker = new ServiceTracker<>(
		_bundleContext, MessageBuilderFactory.class, null);

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

Remember that it's often more desirable to create and register a message
listener as a declarative services component instead of by `new`ing the
`MessageListener` interface and providing an implementation of
`receive(Message)`. If you create your message listener as a declarative
services component you can make your message listener class inherit from
another class or implement multiple interfaces. See step 2 in the Usage section
above for an example of how to create a declarative services component.

#### Step 3: Creating and Registering a Second MessageListener to Listen for the Response

This step is very similar to step 2 above except that the destination response
message listener does not need to accept a message and send a response, it
simply receives the response message.

	MessageListener responseMessageListener = new MessageListener() {
		@Override
		public void receive(Message message) throws MessageListenerException {
			System.out.println("Default response destination received message: " + message);
		}
	};
	
	Dictionary<String, Object> responseListenerProperties = new Hashtable<String, Object>();
	responseListenerProperties.put("destination.name", DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);

	_bundleContext.registerService(MessageListener.class, responseMessageListener, responseListenerProperties);

This message listener simply prints the message received by the response
destination. This allows you to confirm that the response message is what it
should be.

#### Step 4: Create, Populate, and Send a Message to the Destination Registered in Step 1

Creating a message is done exactly the same way as shown earlier. You can
either create a new message directly or you can create a message builder
instead. Here's the direct method:

	Message message = new Message();
	message.setPayload("payload");

	message.setResponseDestinationName(DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
	
To send the message, use the message bus like this:

	Object response = getMessageBus().sendSynchronousMessage(destinationName, message);

Note that `sendSynchronousMessage` returns a response object. In this example,
our message listener defined in step 2 set the response message payload to the
original message so we expect the response to be a `Message`. However, remember
that in general, a message response, like a message payload, can be any object.
If you want to visually check that the message response equals the original
message, print the result of `sendSynchronousMessage`:

	System.out.println("response: " + response);

Instead of creating a message manually and sending it via the message bus
directly, use can use a message builder.

	messageBuilderFactory = getMessageBuilderFactory();
	
	MessageBuilder messageBuilder = messageBuilderFactory.create(destinationName3);
	
	messageBuilder.setPayload("payload");

	messageBuilder.setResponseDestinationName(DestinationNames.MESSAGE_BUS_DEFAULT_RESPONSE);
	
Here, the `getMessageBuilderFactory` method is the same helper method as the
one shown in step 2. It's a standard idiom for obtaining a reference to an OSGi
service.

To send the message, just do this:

	Object response = messageBuilder.sendSynchronous();

Of course, you can check the response as well:

	System.out.println("response: " + response);

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
immediately before and / or after a message is received.

To specify processing that should take place when destinations are added to or
removed from the message bus, simply create a message bus event listener and
register it as an OSGi service:

	MessageBusEventListener listener = new MessageBusEventListener() {
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
	};

	ServiceRegistration<MessageBusEventListener> serviceRegistration =
		_bundleContext.registerService(
			MessageBusEventListener.class, listener, null);

After registering your message bus event listener service, your
`destinationAdded` and `destinationRemoved` methods will be invoked whenever a
destination is added or removed.

Destination event listeners work nearly the same way:

	DestinationEventListener destinationEventListener = new DestinationEventListener() {
		@Override
		public void messageListenerRegistered(String destinationName, MessageListener messageListener) {
			// Your processing here
			System.out.println("Message listener registered with " + destinationName + "!");
		}

		@Override
		public void messageListenerUnregistered(String destinationName, MessageListener messageListener) {
			// Your processing here
			System.out.println("Message listener unregistered with " + destinationName + "!");
		}
	};

	Dictionary<String, Object> destinationEventListenerProperties = new Hashtable<>();

	destinationEventListenerProperties.put("destination.name", destinationName3);

	_bundleContext.registerService(DestinationEventListener.class, destinationEventListener, destinationEventListenerProperties);

One important difference is that while message bus event listeners listen
globally (at the message bus scope) for any destination that are added or
removed, destination event listeners only listen for message listeners that are
added or removed to a particular destination. So you must specify that
destination as a property of your destination event listener service. In the
example above, this was done via the `destinationEventListenerProperties`
dictionary used as the third parameter in the `registerService` invocation.

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

- Immediately before a message is sent: `OutboundMessageProcessor.beforeSend(Message)`
- Immediately after a message is sent: `OutboundMessageProcessor.afterSend(Message)`
- Immediately before a message is received on the same thread (for synchronous destinations): `InboundMessageProcessor.beforeReceive(Message)`
- Immediately after a message is received on the same thread (for synchronous destinations): `InboundMessageProcessor.afterReceive(Message)`
- Immediately before a message is received on another thread (for asynchronous destinations)
- Immediately after a message is received on another thread (for asynchronous destinations)

Here's an example of how to create an outbound message processor:

	OutboundMessageProcessorFactory ompFactory =
			new OutboundMessageProcessorFactory() {
		@Override
		public OutboundMessageProcessor create() {
			return new OutboundMessageProcessor() {
				@Override
				public void afterSend(Message message) throws MessageProcessorException {
					System.out.println("In afterSend!");
				}

				@Override
				public Message beforeSend(Message message) throws MessageProcessorException {
					System.out.println("In beforeSend!");

					message.put("extraKey", "extraValue");

					return message;
				}
			};
		}
	};

	Dictionary<String, Object> ompFactoryProperties = new Hashtable<String, Object>();

	ompFactoryProperties.put("destination.name", destinationName);

	_bundleContext.registerService(
		OutboundMessageProcessorFactory.class, ompFactory, ompFactoryProperties);

In this example, since the outbound message processor is stateless, it's silly
to return a new outbound message processor instance for each invocation of
`OutboundMessageProcessorFactory.create()`. However, for nontrivial use cases,
it's usually important for message processor factories to return new message
processor instances. Notice that the message processor factory must be
registered to a specific destination. This is done using the pattern you've
seen several times already.

Now if you send a message to the destination to which you registered your
message processor, you'll see that the `beforeSend` and `afterSend` methods
were invoked. To check this, use the `getMessageBuilderFactory` method shown
earlier to build and send a message:

	MessageBuilder ompMessageBuilder = getMessageBuilderFactory().create(destinationName);
	
	ompMessageBuilder.setPayload("ompMessagePayload");
	
	ompMessageBuilder.send();

You can also check that the extra key / value pair added to the message in the
`beforeSend` method appears in the received message.

Inbound message processors work similarly to outbound message processors. Just
remember that for asynchronous destinations (parallel and serial destinations),
the `beforeThread` and `afterThread` methods are invoked instead of the
`beforeReceive` and `afterReceive` methods. 
	
<!--
TODO: Will these topics be useful?

### Rejected Execution Handlers

### Executor Service Registar
-->
