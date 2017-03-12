package com.example

import javax.jmdns.ServiceEvent

sealed trait ConnectionState

case class ConnectionAdded(event : ServiceEvent) extends ConnectionState
case class ConnectionResolved(event : ServiceEvent) extends ConnectionState
case class ConnectionRemoved(event : ServiceEvent) extends ConnectionState