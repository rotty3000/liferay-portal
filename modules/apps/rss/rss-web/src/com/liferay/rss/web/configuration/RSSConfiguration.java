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

package com.liferay.rss.web.configuration;

import aQute.bnd.annotation.metatype.Meta;

/**
 * @author Raymond Augé
 * @author Peter Fellwock
 */
@Meta.OCD(id = "com.liferay.rss.web", localization = "content.Language")
public interface RSSConfiguration {

	@Meta.AD(deflt = "8", id = "entriesPerFeed", required = false)
	public String getEntriesPerFeed();

	@Meta.AD(deflt = "1", id = "expandedEntriesPerFeed", required = false)
	public String getExpandedEntriesPerFeed();

	@Meta.AD(deflt = "right", id = "feedImageAlignment", required = false)
	public String getFeedImageAlignment();

	@Meta.AD(deflt = "0, ,", id = "footerArticleValues", required = false)
	public String getFooterArticleValues();

	@Meta.AD(deflt = "0, ,", id = "headerArticleValues", required = false)
	public String getHeaderArticleValues();

	@Meta.AD(id = "titles", required = false)
	public String getTitles();

	@Meta.AD(deflt = "http://www.liferay.com/community/blogs/-/blogs_stream/community/rss, http://rss.news.yahoo.com/rss/tech, http://partners.userland.com/nytRss/technology.xml",
		id = "urls", required = false)
	public String getUrls();

	@Meta.AD(deflt = "true", id = "showFeedDescription", required = false)
	public String showFeedDescription();

	@Meta.AD(deflt = "true", id = "showFeedImage", required = false)
	public String showFeedImage();

	@Meta.AD(deflt = "true", id = "showFeedItemAuthor", required = false)
	public String showFeedItemAuthor();

	@Meta.AD(deflt = "true", id = "showFeedPublishedDate", required = false)
	public String showFeedPublishedDate();

	@Meta.AD(deflt = "true", id = "showFeedTitle", required = false)
	public String showFeedTitle();

}