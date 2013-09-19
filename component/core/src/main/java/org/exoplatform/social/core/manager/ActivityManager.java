/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.core.manager;

import java.util.List;

import org.exoplatform.social.common.RealtimeListAccess;
import org.exoplatform.social.core.ActivityProcessor;
import org.exoplatform.social.core.BaseActivityProcessorPlugin;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.storage.ActivityStorageException;

/**
 * Provides APIs to manage activities.
 * All methods to manipulate with activities, comments and likes are provided. 
 * With these API types, you can store, get and update activities information.
 * You can get an activity by using its Id. You can get a list of activities by the returned result 
 * under <code>ListAccess</code> for lazy loading.
 * Also, the API which adds processors to process activities content is also included.
 *  
 */
public interface ActivityManager {

  /**
   * Saves a newly created activity to a stream. 
   * Stream owner is <code>Activity.userId</code> in case that information has not already set.
   *
   * @param streamOwner The activity stream owner
   * @param activity The activity to be saved
   * @LevelAPI Platform
   * @since  1.2.0-GA
   */
  void saveActivityNoReturn(Identity streamOwner, ExoSocialActivity activity);


   /**
   * Saves a newly created activity to the stream. In this case, information of the stream owner has been set in the activity.
   *
   * @param activity The activity to be saved
   * @LevelAPI Platform
   * @since 1.2.0-GA
   */
  void saveActivityNoReturn(ExoSocialActivity activity);

  /**
   * Saves a new activity by indicating the stream owner, the activity type and title. 
   * This is shorthand to save an activity without creating a new {@link ExoSocialActivity} instance.
   *
   * @param streamOwner The activity stream owner
   * @param type The type of activity
   * @param title The title of activity
   * @LevelAPI Platform
   */
  void saveActivity(Identity streamOwner, String type, String title);

  /**
   * Gets an activity by its Id.
   *
   * @param activityId The Id of activity
   * @return The activity which matches with the provided Id
   * @LevelAPI Platform
   */
  ExoSocialActivity getActivity(String activityId);

  /**
   * Gets an activity by its comment. Comments are considered as children of activities.
   *
   * @param comment The specific comment
   * @return The activity which contains the provided comment
   * @LevelAPI Platform
   * @since  1.2.0-GA
   */
  ExoSocialActivity getParentActivity(ExoSocialActivity comment);

  /**
   * Updates an existing activity.
   *
   * @param activity The activity to be updated
   * @LevelAPI Platform
   * @since 1.2.0-GA
   */
  void updateActivity(ExoSocialActivity activity);

  /**
   * Deletes a specific activity.
   *
   * @param activity The activity to be deleted
   * @LevelAPI Platform
   * @since 1.1.1
   */
  void deleteActivity(ExoSocialActivity activity);

  /**
   * Deletes an activity by its Id.
   *
   * @param activityId The Id of activity to be deleted
   * @LevelAPI Platform
   */
  void deleteActivity(String activityId);

  /**
   * Saves a new comment to a specific activity.
   *
   * @param activity The specific activity
   * @param newComment The new comment to be saved
   * @LevelAPI Platform
   */
  void saveComment(ExoSocialActivity activity, ExoSocialActivity newComment);

  /**
   * Gets comments of a specific activity. 
   * The type of returned result is <code>ListAccess</code> which can be lazy loaded.
   *
   *
   * @param activity The specific activity
   * @return List of comments matching with the given condition
   * @LevelAPI Platform
   * @since 1.2.0-GA
   */
  RealtimeListAccess<ExoSocialActivity> getCommentsWithListAccess(ExoSocialActivity activity);

  /**
   * Deletes an existing comment of a specific activity by its Id.
   *
   * @param activityId Id of an activity containing the comment which is deleted.
   * @param commentId Id of the comment which is deleted.
   * @LevelAPI Platform
   */
  void deleteComment(String activityId, String commentId);

  /**
   * Deletes a comment of a specific activity.
   *
   * @param activity The activity containing the comment which is deleted.
   * @param comment The comment to be deleted.
   * @LevelAPI Platform
   * @since 1.2.0-GA
   */
  void deleteComment(ExoSocialActivity activity, ExoSocialActivity comment);

  /**
   * Saves the like information of an identity to a specific activity.
   *
   * @param activity The activity containing the like information which is saved
   * @param identity The identity who likes this activity
   * @LevelAPI Platform
   */
  void saveLike(ExoSocialActivity activity, Identity identity);

  /**
   * Deletes a like of an identity from a specific activity.
   *
   * @param activity The activity containing the deleted like.
   * @param identity The identity who has the deleted like from a specific activity.
   * @LevelAPI Platform
   * @since 1.2.0-GA
   */
  void deleteLike(ExoSocialActivity activity, Identity identity);

  /**
   * Gets the activities posted on the provided activity stream owner.
   * The type of returned result is <code>ListAccess</code> which can be lazy loaded.
   *
   * @param identity The provided activity stream owner
   * @return Activities on the provided activity stream owner
   * @LevelAPI Platform
   * @since 1.2.0-GA
   */
  RealtimeListAccess<ExoSocialActivity> getActivitiesWithListAccess(Identity identity);
  
  /**
   * Gets activities on the provided activity stream which is viewed by another.
   * The type of returned result is <code>ListAccess</code> which can be lazy loaded.
   * <p><blockquote><pre>
   *    Example: Mary connected with Demo, signed in Demo, and then watch Mary's activity stream
   *</pre></blockquote><p>
   * 
   * @param ownerIdentity The provided activity stream owner
   * @param viewerIdentity The identity who views the other stream
   * @return Activities on the provided activity stream owner
   * @LevelAPI Platform
   * @since 4.0.x
   */
  RealtimeListAccess<ExoSocialActivity> getActivitiesWithListAccess(Identity ownerIdentity, Identity viewerIdentity);

  /**
   * Gets activities posted by all connections with a specific identity.
   * The type of returned result is <code>ListAccess</code> which can be lazy loaded.
   *
   * @param identity The specific identity
   * @return The activities posted by all connections with a specific identity
   * @LevelAPI Platform
   * @since 1.2.0-GA
   */
  RealtimeListAccess<ExoSocialActivity> getActivitiesOfConnectionsWithListAccess(Identity identity);

  /**
   * Gets activities posted on spaces by provided Id of space.
   * The type of returned result is <code>ListAccess</code> which can be lazy loaded.
   *
   * @param spaceIdentity The specific stream owner identity
   * @return  Activities which belong to the provided space
   * @LevelAPI Platform
   * @since 1.2.0-GA
   */
  RealtimeListAccess<ExoSocialActivity> getActivitiesOfSpaceWithListAccess(Identity spaceIdentity);
  
  /**
   * Gets activities posted on all space activity streams in which the provided identity joins.
   * The type of returned result is <code>ListAccess</code> which can be lazy loaded.
   *
   * @param identity The specific user identity to get his activities on spaces
   * @return Activities of the provided user on spaces
   * @LevelAPI Platform
   * @since 4.0.x
   */
  RealtimeListAccess<ExoSocialActivity> getActivitiesOfUserSpacesWithListAccess(Identity identity);

  /**
   * Gets all the activities accessible by a specific identity.
   * The type of returned result is <code>ListAccess</code> which can be lazy loaded.
   *
   * @param identity The specific identity
   * @return All activities of the provided identity
   * @LevelAPI Platform
   * @since 1.2.0-GA
   */
  RealtimeListAccess<ExoSocialActivity> getActivityFeedWithListAccess(Identity identity);

  /**
   * Gets activities by an individual given poster.
   * The type of returned result is <code>ListAccess<code> which can be lazy loaded.
   * 
   * @param poster The identity who posted activities.
   * @return Activities of the user who is poster. 
   * @LevelAPI Platform
   * @since 4.0.1-GA
   */
  RealtimeListAccess<ExoSocialActivity> getActivitiesByPoster(Identity poster);
  
  /**
   * Gets activities by input provided types.
   * The type of returned result is <code>ListAccess<code> which can be lazy loaded.
   * 
   * @param poster The identity who posted activities.
   * @param activityTypes Provided types to get activities.
   * @return Activities of the user who is poster. 
   * @LevelAPI Platform
   * @since 4.0.2-GA, 4.1.x
   */
  RealtimeListAccess<ExoSocialActivity> getActivitiesByPoster(Identity posterIdentity, String ... activityTypes);
  
  /**
   * Adds a new activity processor.
   *
   * @param activityProcessor Activity processor
   * @LevelAPI Platform
   */
  void addProcessor(ActivityProcessor activityProcessor);

  /**
   * Adds a new activity processor plugin.
   *
   * @param activityProcessorPlugin Activity processor plugin
   * @LevelAPI Platform
   */
  void addProcessorPlugin(BaseActivityProcessorPlugin activityProcessorPlugin);

  /**
   * Saves a newly created activity to a stream. Note that the Activity.userId will be set to the owner's identity if it
   * has not already been set.
   *
   * @param streamOwner The activity stream owner
   * @param activity The activity to be saved
   * @return The saved activity
   * @LevelAPI Provisional
   * @deprecated Use {@link #saveActivityNoReturn(Identity, ExoSocialActivity)} instead.
   *             Will be removed by 4.0.x.
   */
  @Deprecated
  ExoSocialActivity saveActivity(Identity streamOwner, ExoSocialActivity activity);


  /**
   * Saves a newly created activity to the stream of that activity's userId stream. The userId of the created activity
   * must be set to indicate the owner stream.
   *
   * @param activity the activity to be saved
   * @LevelAPI Provisional
   * @deprecated Use {@link #saveActivityNoReturn(org.exoplatform.social.core.activity.model.ExoSocialActivity)}
   * instead. Will be removed by 4.0.x.
   */
  @Deprecated
  ExoSocialActivity saveActivity(ExoSocialActivity activity);

  /**
   * Gets all activities by an identity.
   *
   * @param identity The identity
   * @return The activities
   * @see #getActivities(Identity, long, long)
   * @LevelAPI Provisional
   * @deprecated Use {@link #getActivitiesWithListAccess(org.exoplatform.social.core.identity.model.Identity)} instead.
   *             Will be removed by 4.0.x.
   */
  @Deprecated
  List<ExoSocialActivity> getActivities(Identity identity) throws ActivityStorageException;

  /**
   * Gets the latest activities by an identity, specifying the start that is an offset index and the limit.
   *
   * @param identity The identity
   * @param start The offset index
   * @param limit The end-point index
   * @return The activities
   * @LevelAPI Provisional
   * @deprecated Use {@link #getActivitiesWithListAccess(Identity)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  List<ExoSocialActivity> getActivities(Identity identity, long start, long limit) throws ActivityStorageException;

  /**
   * Gets activities of connections from an identity.
   *
   * @param ownerIdentity The identity information to get its activities
   * @return List of activities 
   * @since 1.1.1
   * @LevelAPI Provisional
   * @deprecated Use {@link #getActivitiesOfConnectionsWithListAccess(Identity)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  List<ExoSocialActivity> getActivitiesOfConnections(Identity ownerIdentity) throws ActivityStorageException;


  /**
   * Gets activities of connections from an identity.
   *
   * @param ownerIdentity The identity information to get its activities
   * @return activityList List of activities
   * @LevelAPI Provisional
   * @deprecated Use {@link #getActivitiesOfConnectionsWithListAccess(Identity)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  List<ExoSocialActivity> getActivitiesOfConnections(Identity ownerIdentity,
                                                     int offset, int length) throws ActivityStorageException;

  /**
   * Gets the activities from all spaces of a user.
   *
   * @param ownerIdentity The identity information to get its activities
   * @return List of activities
   * @since 1.1.1
   * @LevelAPI Provisional
   * @deprecated Use {@link #getActivitiesOfUserSpacesWithListAccess(Identity)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  List<ExoSocialActivity> getActivitiesOfUserSpaces(Identity ownerIdentity);

  /**
   * Gets the activity feed of an identity. This feed is the combination of all the activities of his own activities,
   * his connections' activities and his spaces' activities, which are returned, are sorted starting from the most recent.
   *
   * @param identity The identity information to get its activity
   * @return All related activities of the identity, such as activities, connections' activities, and spaces' activities
   * @since 1.1.2
   * @LevelAPI Provisional
   * @deprecated Use {@link #getActivityFeedWithListAccess(Identity)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  List<ExoSocialActivity> getActivityFeed(Identity identity) throws ActivityStorageException;

  /**
   * Removes an identity who likes an activity.
   *
   * @param activity The activity which is liked by the identity
   * @param identity The identity who dislikes the activity
   * @LevelAPI Provisional
   * @deprecated Use {@link #deleteLike(ExoSocialActivity, Identity)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  void removeLike(ExoSocialActivity activity, Identity identity) throws ActivityStorageException;

  /**
   * Gets all comments of an activity.
   *
   * @param activity The activity which you want to get its comments
   * @return List of comments
   * @LevelAPI Provisional
   * @deprecated Use {@link #getCommentsWithListAccess(ExoSocialActivity)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  List<ExoSocialActivity> getComments(ExoSocialActivity activity) throws ActivityStorageException;

  /**
   * Records an activity.
   *
   * @param owner The owner of activity which is recorded
   * @param type The type of activity which is recorded
   * @param title The title of activity which is recorded
   * @return
   * @throws ActivityStorageException
   * @since 1.2.0-Beta1
   * @LevelAPI Provisional
   * @deprecated Use {@link #saveActivity(Identity, String, String)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  ExoSocialActivity recordActivity(Identity owner, String type, String title) throws ActivityStorageException;

  /**
   * Saves an activity.
   *
   * @param owner The owner of activity which is saved.
   * @param activity The activity to be saved
   * @return The stored activity
   * @throws Exception
   * @LevelAPI Provisional
   * @deprecated use {@link #saveActivity(Identity, ExoSocialActivity)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  ExoSocialActivity recordActivity(Identity owner, ExoSocialActivity activity) throws Exception;

  /**
   * Records an activity.
   *
   * @param owner The owner of the target stream for this activity
   * @param type  The type of activity which will be used to use custom UI for rendering
   * @param title The title
   * @param body  The body
   * @return The stored activity
   * @LevelAPI Provisional
   * @deprecated Use {@link #saveActivity(Identity, ExoSocialActivity)} instead. Will be removed by 4.0.x.
   */
  @Deprecated
  ExoSocialActivity recordActivity(Identity owner, String type, String title,
                                   String body) throws ActivityStorageException;

  /**
   * Gets the number of activities from a stream owner.
   *
   * @param owner The identity 
   * @return The number of activities
   * @LevelAPI Provisional
   * @deprecated Will be removed by 4.0.x.
   */
  @Deprecated
  int getActivitiesCount(Identity owner) throws ActivityStorageException;

  /**
   * Process activities to some given rules.
   *
   * @param activity The activity to be processed
   * @LevelAPI Provisional
   * @deprecated Will be removed by 4.0.x.
   */
  @Deprecated
  void processActivitiy(ExoSocialActivity activity);
}
