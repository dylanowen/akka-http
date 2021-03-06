/*
 * Copyright (C) 2017-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.http.javadsl.settings

import akka.actor.ActorSystem
import akka.annotation.{ ApiMayChange, DoNotInherit }
import akka.http.impl.settings.ConnectionPoolSettingsImpl
import com.typesafe.config.Config

import scala.concurrent.duration.Duration
import akka.http.impl.util.JavaMapping.Implicits._
import akka.http.javadsl.ClientTransport

import scala.concurrent.duration.FiniteDuration

@ApiMayChange
trait PoolImplementation
@ApiMayChange
object PoolImplementation {
  def Legacy: PoolImplementation = akka.http.scaladsl.settings.PoolImplementation.Legacy
  def New: PoolImplementation = akka.http.scaladsl.settings.PoolImplementation.New
}

/**
 * Public API but not intended for subclassing
 */
@DoNotInherit
abstract class ConnectionPoolSettings private[akka] () { self: ConnectionPoolSettingsImpl ⇒
  def getMaxConnections: Int = maxConnections
  def getMinConnections: Int = minConnections
  def getMaxRetries: Int = maxRetries
  def getMaxOpenRequests: Int = maxOpenRequests
  def getPipeliningLimit: Int = pipeliningLimit
  def getBaseConnectionBackoff: FiniteDuration = baseConnectionBackoff
  def getMaxConnectionBackoff: FiniteDuration = maxConnectionBackoff
  def getIdleTimeout: Duration = idleTimeout
  def getConnectionSettings: ClientConnectionSettings = connectionSettings

  @ApiMayChange
  def getPoolImplementation: PoolImplementation = poolImplementation

  @ApiMayChange
  def getResponseEntitySubscriptionTimeout: Duration = responseEntitySubscriptionTimeout

  /**
   * The underlying transport used to connect to hosts. By default [[ClientTransport.TCP]] is used.
   */
  @deprecated("Deprecated in favor of getConnectionSettings.getTransport.", "10.1.0")
  @Deprecated
  def getTransport: ClientTransport = transport.asJava

  // ---

  def withMaxConnections(n: Int): ConnectionPoolSettings = self.copy(maxConnections = n)
  def withMinConnections(n: Int): ConnectionPoolSettings = self.copy(minConnections = n)
  def withMaxRetries(n: Int): ConnectionPoolSettings = self.copy(maxRetries = n)
  def withMaxOpenRequests(newValue: Int): ConnectionPoolSettings = self.copy(maxOpenRequests = newValue)
  /** Client-side pipelining is not currently supported, see https://github.com/akka/akka-http/issues/32 */
  def withPipeliningLimit(newValue: Int): ConnectionPoolSettings = self.copy(pipeliningLimit = newValue)
  def withBaseConnectionBackoff(newValue: FiniteDuration): ConnectionPoolSettings = self.copy(baseConnectionBackoff = newValue)
  def withMaxConnectionBackoff(newValue: FiniteDuration): ConnectionPoolSettings = self.copy(maxConnectionBackoff = newValue)
  def withIdleTimeout(newValue: Duration): ConnectionPoolSettings = self.copy(idleTimeout = newValue)
  def withConnectionSettings(newValue: ClientConnectionSettings): ConnectionPoolSettings = self.copy(connectionSettings = newValue.asScala)

  @ApiMayChange
  def withPoolImplementation(newValue: PoolImplementation): ConnectionPoolSettings = self.copy(poolImplementation = newValue.asScala)

  @ApiMayChange
  def withResponseEntitySubscriptionTimeout(newValue: Duration): ConnectionPoolSettings = self.copy(responseEntitySubscriptionTimeout = newValue)

  def withTransport(newValue: ClientTransport): ConnectionPoolSettings = withUpdatedConnectionSettings(_.withTransport(newValue.asScala))
}

object ConnectionPoolSettings extends SettingsCompanion[ConnectionPoolSettings] {
  override def create(config: Config): ConnectionPoolSettings = ConnectionPoolSettingsImpl(config)
  override def create(configOverrides: String): ConnectionPoolSettings = ConnectionPoolSettingsImpl(configOverrides)
  override def create(system: ActorSystem): ConnectionPoolSettings = create(system.settings.config)
}
