/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.spa.senna;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		"event.topics=*",
		"osgi.http.whiteboard.context.select=senna",
		"osgi.http.whiteboard.servlet.pattern=/examples/events/stream"
	},
	service = {EventHandler.class, Servlet.class}
)
public class EventServlet extends HttpServlet implements EventHandler {

	@Override
	public void handleEvent(Event event) {
		for (AsyncContext asyncContext : _contexts) {
			try {
				ServletResponse response = asyncContext.getResponse();

				response.setContentType("text/event-stream");

				PrintWriter writer = response.getWriter();

				writer.println("data: " + event.toString());
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void service(
			HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {

		response.setContentType("text/event-stream");

		final AsyncContext asyncContext = request.startAsync();

		asyncContext.addListener(
			new AsyncListener() {

				@Override
				public void onTimeout(AsyncEvent asyncEvent)
					throws IOException {

					System.out.println("timeout: " + asyncEvent);

					asyncContext.complete();
				}

				@Override
				public void onStartAsync(AsyncEvent asyncEvent)
					throws IOException {

					System.out.println("startAsync: " + asyncEvent);
				}

				@Override
				public void onError(AsyncEvent asyncEvent)
					throws IOException {

					System.out.println("error: " + asyncEvent);
				}

				@Override
				public void onComplete(AsyncEvent asyncEvent)
					throws IOException {

					System.out.println("complete: " + asyncEvent);

					_contexts.remove(asyncContext);
				}
			}
		);

		_contexts.add(asyncContext);
	}

	private List<AsyncContext> _contexts = new CopyOnWriteArrayList<>();

}