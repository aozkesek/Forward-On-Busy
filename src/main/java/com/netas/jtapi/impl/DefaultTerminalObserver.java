package com.netas.jtapi.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.telephony.CallObserver;
import javax.telephony.MethodNotSupportedException;
import javax.telephony.ResourceUnavailableException;
import javax.telephony.Terminal;
import javax.telephony.TerminalObserver;
import javax.telephony.events.CallEv;
import javax.telephony.events.TermEv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.netas.jtapi.intf.ITerminalObserverCommand;

@Component
@Scope("prototype")
public class DefaultTerminalObserver implements TerminalObserver, CallObserver {

	public static NullPointerException TerminalIsNullException = new NullPointerException("Terminal");
	
	@Autowired
	private DefaultProviderObserver providerObserver;
	@Autowired
	@Qualifier("ForwardOnBusyOtherAddressCommand")
	private ITerminalObserverCommand forwardOnBusyOtherAddressCommand;

	private String terminalName;
	private Terminal terminal;
	
	private static Logger logger = LoggerFactory.getLogger(DefaultTerminalObserver.class);
	
	
	public DefaultTerminalObserver(String terminalName) {
		
		if (terminalName == null)
			throw DefaultTerminalObserver.TerminalIsNullException;
		
		this.terminalName = terminalName;
		logger.debug("DefaultTerminalObserver for {}", terminalName);
	}
	
	@PostConstruct
	public void register() {
		
		if (providerObserver == null)
			throw DefaultProviderObserver.ProviderIsNullException;
		
		terminal = providerObserver.getTerminal(terminalName);
		
		if (terminal == null)
			throw DefaultTerminalObserver.TerminalIsNullException;
		
		try {
			terminal.addObserver(this);
			terminal.addCallObserver(this);
		} catch (ResourceUnavailableException e) {
			logger.error("Exception for {} is {}", terminalName, e);
		} catch (MethodNotSupportedException e) {
			logger.error("Exception for {} is {}", terminalName, e);
		}
	
	}
	
	@PreDestroy
	public void unregister() {
		
		terminal.removeObserver(this);
		
	}
	
	public String getTerminalName() {
		return terminalName;
	}
	
	@Override
	public void terminalChangedEvent(TermEv[] termEvents) {
		
		if (termEvents == null)
			return;
		
		for (TermEv terminalEvent : termEvents) {
			logger.info("terminalChangedEvent: {} for {}", terminalEvent, terminalName);
			
		}
		
	}

	@Override
	public void callChangedEvent(CallEv[] callEvents) {
		
		if (callEvents == null)
			return;
		
		for (CallEv callEvent : callEvents) {
			logger.info("callChangedEvent: {} for {}", callEvent, terminalName);
			
			forwardOnBusyOtherAddressCommand.executeCommand(this, callEvent);
			
		}
		
		
	}

}
