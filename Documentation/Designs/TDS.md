Notes
=====

The TDS code will look something like this:

	while (true) {
		receive message
		get exchange label
		check state of exchange from database matching label
			read protocol, step from state
			switch (protocol) {
				case: 'CoffeySaidha'
					call CoffeySaidha(userid, step, message)
			}
	}

	function CoffeySaidha(userid, step, message) {
		switch (step) {
			case: 0
				// Alice requests an exchange with Bob; its signed with her private key SigA(request)
				// Check that we recognise Alice's userid
				// Check that we can use her public key to decrypt her signed message
				// Check that we recognise Bob's userid (from the message)
				if (ok) generate label=UUID and send to Alice
				else send error to Alice
				
			case: 1
				...
		}
	}
