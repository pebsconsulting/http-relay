/*
 * Copyright 2016 Attribyte, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 */

package com.attribyte.relay.wp;

import com.attribyte.client.ClientProtos;
import org.joda.time.DateTime;

import java.util.Properties;

/**
 * Site metadata.
 */
public class SiteMeta {

   /**
    * Creates site metadata from properties.
    * @param props The properties.
    */
   public SiteMeta(final Properties props) {
      this.id = Long.parseLong(props.getProperty("id", "0"));
      this.baseURL = props.getProperty("baseURL");
      this.title = props.getProperty("title");
      this.description = props.getProperty("description");
      this.permalinkStructure = props.getProperty("permalinkStructure");
      long defaultCategoryId = Long.parseLong(props.getProperty("defaultCategoryId", "0"));
      String defaultCategoryName = props.getProperty("defaultCategoryName");
      String defaultCategorySlug = props.getProperty("defaultCategorySlug");
      this.defaultCategory = defaultCategoryId > 0 ? new TermMeta(defaultCategoryId, defaultCategoryName, defaultCategorySlug, 0) : null;
   }

   public SiteMeta(final long id,
                   final String baseURL, final String title,
                   final String description,
                   final String permalinkStructure,
                   final TermMeta defaultCategory) {
      this.id = id;
      this.baseURL = baseURL;
      this.title = title;
      this.description = description;
      this.permalinkStructure = permalinkStructure;
      this.defaultCategory = defaultCategory;
   }

   /**
    * Overrides values in this with those in another, if set.
    * @param other The other site meta.
    * @return The site meta with overrides applied.
    */
   public SiteMeta overrideWith(final SiteMeta other) {
      return new SiteMeta(
              other.id > 0L ? other.id : this.id,
              other.baseURL != null ? other.baseURL : this.baseURL,
              other.title != null ? other.title : this.title,
              other.description != null ? other.description : this.description,
              other.permalinkStructure != null ? other.permalinkStructure : this.permalinkStructure,
              other.defaultCategory != null ? other.defaultCategory : this.defaultCategory
      );
   }

   /**
    * The site id.
    */
   public final long id;

   /**
    * The base (home) URL for the site.
    */
   public final String baseURL;

   /**
    * The site title.
    */
   public final String title;

   /**
    * The site description.
    */
   public final String description;

   /**
    * The permalink format string.
    */
   public final String permalinkStructure;

   /**
    * The default category.
    */
   public final TermMeta defaultCategory;


   /*
      %year%
         The year of the post, four digits, for example 2004
      %monthnum%
         Month of the year, for example 05
      %day%
         Day of the month, for example 28
      %hour%
         Hour of the day, for example 15
      %minute%
         Minute of the hour, for example 43
      %second%
         Second of the minute, for example 33
      %post_id%
         The unique ID # of the post, for example 423
      %postname%
         A sanitized version of the title of the post (post slug field on Edit Post/Page panel). So “This Is A Great Post!” becomes this-is-a-great-post in the URI.
      %category%
         A sanitized version of the category name (category slug field on New/Edit Category panel). Nested sub-categories appear as nested directories in the URI.
      %author%
         A sanitized version of the author name.
    */

   /**
    * Builds the permalink for an entry.
    * @param entry The entry (or entry builder).
    * @return The permalink string.
    * @see <a href="https://codex.wordpress.org/Using_Permalinks">https://codex.wordpress.org/Using_Permalinks</a>
    */
   public String buildPermalink(final ClientProtos.WireMessage.EntryOrBuilder entry,
                                final String postSlug,
                                final String authorSlug,
                                final String category) {

      final String post_id = entry.getUID().getId();
      final DateTime publishTime = new DateTime(entry.getPublishTimeMillis());
      final String year = Integer.toString(publishTime.getYear());
      final String monthnum = String.format("%02d", publishTime.getMonthOfYear());
      final String day = String.format("%02d", publishTime.getDayOfMonth());
      final String hour = String.format("%02d", publishTime.getHourOfDay());
      final String minute = String.format("%02d", publishTime.getMinuteOfHour());
      final String second = String.format("%02d", publishTime.getSecondOfMinute());
      final String path = permalinkStructure
              .replace("%year%", year)
              .replace("%monthnum%", monthnum)
              .replace("%day%", day)
              .replace("%hour%", hour)
              .replace("%minute%", minute)
              .replace("%second%", second)
              .replace("%post_id%", post_id)
              .replace("%postname%", postSlug)
              .replace("%category%", category)
              .replace("%author%", authorSlug);
      return baseURL + path;
   }
}