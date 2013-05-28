/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.social.core.storage.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.chromattic.api.query.Query;
import org.chromattic.api.query.QueryBuilder;
import org.chromattic.api.query.QueryResult;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.activity.filter.ActivityFilter;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.chromattic.entity.ActivityEntity;
import org.exoplatform.social.core.chromattic.entity.ActivityRef;
import org.exoplatform.social.core.chromattic.entity.ActivityRefListEntity;
import org.exoplatform.social.core.chromattic.entity.IdentityEntity;
import org.exoplatform.social.core.chromattic.filter.JCRFilterLiteral;
import org.exoplatform.social.core.chromattic.utils.ActivityRefList;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.storage.ActivityStorageException;
import org.exoplatform.social.core.storage.api.ActivityStorage;
import org.exoplatform.social.core.storage.api.ActivityStreamStorage;
import org.exoplatform.social.core.storage.api.RelationshipStorage;
import org.exoplatform.social.core.storage.exception.NodeNotFoundException;

public class ActivityStreamStorageImpl extends AbstractStorage implements ActivityStreamStorage {
  
  /**
   * The identity storage
   */
  private final IdentityStorageImpl identityStorage;
  

  /**
   * The relationship storage
   */
  private final RelationshipStorage relationshipStorage;
  
  /**
   * The activity storage
   */
  private ActivityStorage activityStorage;
  
  /** Logger */
  private static final Log LOG = ExoLogger.getLogger(ActivityStreamStorageImpl.class);
  
  public ActivityStreamStorageImpl(IdentityStorageImpl identityStorage, RelationshipStorage relationshipStorage) {
    this.identityStorage = identityStorage;
    this.relationshipStorage = relationshipStorage;
  }
  
  private ActivityStorage getStorage() {
    if (activityStorage == null) {
      activityStorage = (ActivityStorage) PortalContainer.getInstance().getComponentInstanceOfType(ActivityStorage.class);
    }
    
    return activityStorage;
  }

  @Override
  public void save(Identity owner, ExoSocialActivity activity) {
    try {
      //
      ActivityEntity activityEntity = _findById(ActivityEntity.class, activity.getId());     
      createOwnerRefs(owner, activityEntity);
      
      //
      List<Identity> got = relationshipStorage.getConnections(owner);
      if (got.size() > 0) {
        createConnectionsRefs(got, activityEntity);
      }
    } catch (NodeNotFoundException e) {
      LOG.warn("Failed to add Activity references.");
    }
  }
  
  @Override
  public void delete(Identity owner, ExoSocialActivity activity) {
    
    try {
      //
      ActivityEntity activityEntity = _findById(ActivityEntity.class, activity.getId());     
      removeOwnerRefs(owner, activityEntity);
      
      //
      List<Identity> got = relationshipStorage.getConnections(owner);
      if (got.size() > 0) {
        removeConnectionsRefs(got, activityEntity);
      }
    } catch (NodeNotFoundException e) {
      LOG.warn("Failed to delete Activity references.");
    }
  }
  
  

  @Override
  public void update(Identity owner) {
  }

  @Override
  public List<ExoSocialActivity> getFeed(Identity owner, int offset, int limit) {
    List<ExoSocialActivity> got = new LinkedList<ExoSocialActivity>();
    try {
      IdentityEntity identityEntity = identityStorage._findIdentityEntity(OrganizationIdentityProvider.NAME, owner.getRemoteId());
      ActivityRefListEntity refList = ActivityRefType.FEED.refsOf(identityEntity);
      ActivityRefList list = new ActivityRefList(refList);
      
      int nb = 0;
      
      Iterator<ActivityRef> it = list.iterator();

      _skip(it, offset);

      while (it.hasNext()) {
        ActivityRef current = it.next();

        //take care in the case, current.getActivityEntity() = null the same SpaceRef, need to remove it out
        if (current.getActivityEntity() == null) {
          current.getDay().getActivityRefs().remove(current.getName());
          continue;
        }
        
        got.add(getStorage().getActivity(current.getActivityEntity().getId()));

        if (++nb == limit) {
          break;
        }

      }
      
    } catch (NodeNotFoundException e) {
      LOG.warn("Failed to getFeed()");
    }
    
    return got;
  }

  @Override
  public void getNumberOfFeed(Identity owner) {
    
  }

  @Override
  public List<ExoSocialActivity> getConnections(Identity owner, int offset, int limit) {
    List<ExoSocialActivity> got = new LinkedList<ExoSocialActivity>();
    try {
      IdentityEntity identityEntity = identityStorage._findIdentityEntity(OrganizationIdentityProvider.NAME, owner.getRemoteId());
      ActivityRefListEntity refList = ActivityRefType.CONNECTION.refsOf(identityEntity);
      ActivityRefList list = new ActivityRefList(refList);
      
      int nb = 0;
      
      Iterator<ActivityRef> it = list.iterator();

      _skip(it, offset);

      while (it.hasNext()) {
        ActivityRef current = it.next();

        //take care in the case, current.getActivityEntity() = null the same SpaceRef, need to remove it out
        if (current.getActivityEntity() == null) {
          current.getDay().getActivityRefs().remove(current.getName());
          continue;
        }
        
        //
        got.add(getStorage().getActivity(current.getActivityEntity().getId()));

        if (++nb == limit) {
          break;
        }

      }
      
    } catch (NodeNotFoundException e) {
      LOG.warn("Failed to getFeed()");
    }
    
    return got;
  }

  @Override
  public int getNumberOfConnections(Identity owner) {
    return 0;
  }

  @Override
  public List<ExoSocialActivity> getSpaces(Identity owner, int offset, int limit) {
    return null;
  }

  @Override
  public int getNumberOfSpaces(Identity owner) {
    return 0;
  }

  @Override
  public List<ExoSocialActivity> getMyActivities(Identity owner, int offset, int limit) {
    return null;
  }

  @Override
  public int getNumberOfMyActivities(Identity owner) {
    return 0;
  }

  @Override
  public void connect(Identity sender, Identity receiver) {
    
    try {
      //
      List<ExoSocialActivity> activities = getActivitiesOfConnections(sender);
      for (ExoSocialActivity activity : activities) {
        
        ActivityEntity entity = _findById(ActivityEntity.class, activity.getId());
        
        createConnectionsRefs(receiver, entity);
      }
      
      //
      activities = getActivitiesOfConnections(receiver);
      for (ExoSocialActivity activity : activities) {
        
        ActivityEntity entity = _findById(ActivityEntity.class, activity.getId());
        
        createConnectionsRefs(sender, entity);
      }
    } catch (NodeNotFoundException e) {
      LOG.warn("Failed to add Activity references when create relationship.");
    }
  }
  
  @Override
  public void deleteConnect(Identity sender, Identity receiver) {
    
    try {
      //
      List<ExoSocialActivity> activities = getActivitiesOfConnections(sender);
      for (ExoSocialActivity activity : activities) {
        
        ActivityEntity entity = _findById(ActivityEntity.class, activity.getId());
        
        removeConnectionsRefs(receiver, entity);
      }
      
      //
      activities = getActivitiesOfConnections(receiver);
      for (ExoSocialActivity activity : activities) {
        
        ActivityEntity entity = _findById(ActivityEntity.class, activity.getId());
        
        removeConnectionsRefs(sender, entity);
      }
    } catch (NodeNotFoundException e) {
      LOG.warn("Failed to delete Activity references when delete relationship.");
    }
  }
  
  /**
   * The reference types.
   */
  public enum ActivityRefType {
    FEED() {
      @Override
      public ActivityRefListEntity refsOf(IdentityEntity identityEntity) {
        return identityEntity.getAllStream();
      }
    },
    CONNECTION() {
      @Override
      public ActivityRefListEntity refsOf(IdentityEntity identityEntity) {
        return identityEntity.getConnectionStream();
      }
    },
    MY_SPACES() {
      @Override
      public ActivityRefListEntity refsOf(IdentityEntity identityEntity) {
        return identityEntity.getSpaceStream();
      }
    },
    MY_ACTIVITIES() {
      @Override
      public ActivityRefListEntity refsOf(IdentityEntity identityEntity) {
        return identityEntity.getMyStream();
      }
    };

    public abstract ActivityRefListEntity refsOf(IdentityEntity identityEntity);
  }
  
  /**
   * {@inheritDoc}
   */
  private List<ExoSocialActivity> getActivitiesOfConnections(Identity ownerIdentity) {

    List<Identity> connections = new ArrayList<Identity>();
    
    if (ownerIdentity == null ) {
      return Collections.emptyList();
    }
    
    connections.add(ownerIdentity);
    
    //
    ActivityFilter filter = new ActivityFilter(){};

    //
    return getActivitiesOfIdentities(ActivityBuilderWhere.simple().owners(connections), filter, 0, -1);
  }
  
  /**
   * {@inheritDoc}
   */
  private List<ExoSocialActivity> getActivitiesOfIdentities(ActivityBuilderWhere where, ActivityFilter filter,
                                                           long offset, long limit) throws ActivityStorageException {

    QueryResult<ActivityEntity> results = getActivitiesOfIdentitiesQuery(where, filter).objects(offset, limit);

    List<ExoSocialActivity> activities =  new ArrayList<ExoSocialActivity>();

    while(results.hasNext()) {
      activities.add(getStorage().getActivity(results.next().getId()));
    }

    return activities;
  }
  
  
  private Query<ActivityEntity> getActivitiesOfIdentitiesQuery(ActivityBuilderWhere whereBuilder,
                                                               JCRFilterLiteral filter) throws ActivityStorageException {

    QueryBuilder<ActivityEntity> builder = getSession().createQueryBuilder(ActivityEntity.class);

    builder.where(whereBuilder.build(filter));
    whereBuilder.orderBy(builder, filter);

    return builder.get();
  }
  
  private void createOwnerRefs(Identity owner, ActivityEntity activityEntity) throws NodeNotFoundException {
    manageRefList(new UpdateContext(owner, null), activityEntity, ActivityRefType.FEED);
    manageRefList(new UpdateContext(owner, null), activityEntity, ActivityRefType.MY_ACTIVITIES);
  }
  
  private void createConnectionsRefs(List<Identity> identities, ActivityEntity activityEntity) throws NodeNotFoundException {
    manageRefList(new UpdateContext(identities, null), activityEntity, ActivityRefType.FEED);
    manageRefList(new UpdateContext(identities, null), activityEntity, ActivityRefType.CONNECTION);
  }
  
  private void createConnectionsRefs(Identity identity, ActivityEntity activityEntity) throws NodeNotFoundException {
    manageRefList(new UpdateContext(identity, null), activityEntity, ActivityRefType.FEED);
    manageRefList(new UpdateContext(identity, null), activityEntity, ActivityRefType.CONNECTION);
  }
  
  private void removeConnectionsRefs(Identity identity, ActivityEntity activityEntity) throws NodeNotFoundException {
    manageRefList(new UpdateContext(null, identity), activityEntity, ActivityRefType.FEED);
    manageRefList(new UpdateContext(null, identity), activityEntity, ActivityRefType.CONNECTION);
  }
  
  private void removeOwnerRefs(Identity owner, ActivityEntity activityEntity) throws NodeNotFoundException {
    manageRefList(new UpdateContext(null, owner), activityEntity, ActivityRefType.FEED);
    manageRefList(new UpdateContext(null, owner), activityEntity, ActivityRefType.MY_ACTIVITIES);
  }
  
  private void removeConnectionsRefs(List<Identity> identities, ActivityEntity activityEntity) throws NodeNotFoundException {
    manageRefList(new UpdateContext(null, identities), activityEntity, ActivityRefType.CONNECTION);
  }
  
  private void manageRefList(UpdateContext context, ActivityEntity activityEntity, ActivityRefType type) throws NodeNotFoundException {

    if (context.getAdded() != null) {
      for (Identity identity : context.getAdded()) {
        IdentityEntity identityEntity = identityStorage._findIdentityEntity(OrganizationIdentityProvider.NAME, identity.getRemoteId());

        ActivityRefListEntity listRef = type.refsOf(identityEntity);
        ActivityRef ref = listRef.get(activityEntity);
        if (!ref.getName().equals(activityEntity.getName())) {
          ref.setName(activityEntity.getName());
        }

        if (ref.getLastUpdated() == null || ref.getLastUpdated().longValue() != activityEntity.getLastUpdated().longValue()) {
          ref.setLastUpdated(activityEntity.getLastUpdated());
        }

        ref.setActivityEntity(activityEntity);

      }

      for (Identity identity : context.getRemoved()) {
        try {
          IdentityEntity identityEntity = identityStorage._findIdentityEntity(OrganizationIdentityProvider.NAME, identity.getRemoteId());
          
          ActivityRefListEntity listRef = type.refsOf(identityEntity);
          listRef.remove(activityEntity);
        }
        catch (NodeNotFoundException e) {
          LOG.warn(e.getMessage(), e);
        }
      }
    }
  }
  
  private class UpdateContext {
    private List<Identity> added;
    private List<Identity> removed;

    private UpdateContext(List<Identity> added, List<Identity> removed) {
      this.added = added;
      this.removed = removed;
    }
    
    private UpdateContext(Identity added, Identity removed) {
      if (added != null) {
        this.added = new CopyOnWriteArrayList<Identity>();
        this.added.add(added);
      }
      
      //
      if (removed != null) {
        this.removed = new CopyOnWriteArrayList<Identity>();
        this.removed.add(removed);
      }
    }

    public List<Identity> getAdded() {
      return added == null ? new ArrayList<Identity>() : added;
    }

    public List<Identity> getRemoved() {
      return removed == null ? new ArrayList<Identity>() : removed;
    }
  }

}
