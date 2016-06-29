/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.core.plugins.orchestrate.akka;

import org.roda.core.index.IndexService;
import org.roda.core.model.ModelService;
import org.roda.core.plugins.Plugin;
import org.roda.core.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class AkkaWorkerActor extends UntypedActor {
  private static final Logger LOGGER = LoggerFactory.getLogger(AkkaWorkerActor.class);

  private final IndexService index;
  private final ModelService model;
  private final StorageService storage;

  public AkkaWorkerActor(StorageService storage, ModelService model, IndexService index) {
    this.storage = storage;
    this.model = model;
    this.index = index;
  }

  @Override
  public void onReceive(Object msg) throws Exception {
    if (msg instanceof Messages.PluginBeforeBlockExecuteIsReady) {
      handlePluginBeforeBlockExecuteIsReady(msg);
    } else if (msg instanceof Messages.PluginExecuteIsReady) {
      handlePluginExecuteIsReady(msg);
    } else if (msg instanceof Messages.PluginAfterBlockExecuteIsReady) {
      handlePluginAfterBlockExecuteIsReady(msg);
    } else if (msg instanceof Messages.PluginAfterAllExecuteIsReady) {
      handlePluginAfterAllExecuteIsReady(msg);
    } else {
      LOGGER.error("Received a message that it doesn't know how to process (" + msg.getClass().getName() + ")...");
      unhandled(msg);
    }
  }

  private void handlePluginBeforeBlockExecuteIsReady(Object msg) {
    Messages.PluginBeforeBlockExecuteIsReady message = (Messages.PluginBeforeBlockExecuteIsReady) msg;
    Plugin<?> plugin = message.getPlugin();
    ActorRef sender = getSender();
    ActorRef self = getSelf();
    ActorRef parent = getContext().parent();
    try {
      plugin.beforeBlockExecute(index, model, storage);
      sender.tell(new Messages.PluginBeforeBlockExecuteIsDone(plugin, false, message.getList()), parent);
    } catch (Exception e) {
      LOGGER.error("Error executing plugin.beforeBlockExecute()", e);
      sender.tell(new Messages.PluginBeforeBlockExecuteIsDone(plugin, true, message.getList()), parent);
    }
  }

  private void handlePluginExecuteIsReady(Object msg) {
    Messages.PluginExecuteIsReady message = (Messages.PluginExecuteIsReady) msg;
    Plugin<?> plugin = message.getPlugin();
    try {
      plugin.execute(index, model, storage, message.getList());
      getSender().tell(new Messages.PluginExecuteIsDone(plugin, false), getSelf());
    } catch (Exception e) {
      LOGGER.error("Error executing plugin.execute()", e);
      getSender().tell(new Messages.PluginExecuteIsDone(plugin, true), getSelf());
    }
  }

  private void handlePluginAfterBlockExecuteIsReady(Object msg) {
    Messages.PluginAfterBlockExecuteIsReady message = (Messages.PluginAfterBlockExecuteIsReady) msg;
    Plugin<?> plugin = message.getPlugin();
    try {
      plugin.afterBlockExecute(index, model, storage);
      getSender().tell(new Messages.PluginAfterBlockExecuteIsDone(plugin, false), getSelf());
    } catch (Exception e) {
      LOGGER.error("Error executing plugin.afterBlockExecute()", e);
      getSender().tell(new Messages.PluginAfterBlockExecuteIsDone(plugin, true), getSelf());
    }
  }

  private void handlePluginAfterAllExecuteIsReady(Object msg) {
    Messages.PluginAfterAllExecuteIsReady message = (Messages.PluginAfterAllExecuteIsReady) msg;
    Plugin<?> plugin = message.getPlugin();
    try {
      plugin.afterAllExecute(index, model, storage);
      getSender().tell(new Messages.PluginAfterAllExecuteIsDone(plugin, false), getSelf());
    } catch (Exception e) {
      LOGGER.error("Error executing plugin.afterAllExecute()", e);
      getSender().tell(new Messages.PluginAfterAllExecuteIsDone(plugin, true), getSelf());
    }
  }

}
