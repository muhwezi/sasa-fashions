/**
 * Provides TCP networking functionality for the
 * Sasa Fashions Management System.
 *
 * <p>The networking package allows approved client programs
 * to communicate with the Sasa Fashions application server
 * without receiving direct access to the MySQL database.</p>
 *
 * <p>The principal classes include:</p>
 *
 * <ul>
 *     <li>
 *         {@link com.sasafashions.network.SasaNetworkServer}
 *         - listens for TCP connections
 *     </li>
 *
 *     <li>
 *         {@link com.sasafashions.network.ClientHandler}
 *         - handles one connected client
 *     </li>
 *
 *     <li>
 *         {@link com.sasafashions.network.NetworkDataService}
 *         - retrieves approved business information
 *     </li>
 *
 *     <li>
 *         {@link com.sasafashions.network.NetworkClient}
 *         - command-line testing client
 *     </li>
 *
 *     <li>
 *         {@link com.sasafashions.network.NetworkClientFrame}
 *         - graphical Swing network client
 *     </li>
 * </ul>
 *
 * <p>The package demonstrates TCP sockets, client-server
 * architecture, multithreading, interfaces, Swing,
 * event-driven programming, JDBC integration, exception
 * handling and separation of concerns.</p>
 *
 * @author SASA Group
 * @version 1.3
 */
package com.sasafashions.network;