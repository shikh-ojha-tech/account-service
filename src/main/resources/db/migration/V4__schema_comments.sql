COMMENT ON TABLE account IS 'Customer bank accounts';
COMMENT ON COLUMN account.account_id IS 'Unique account id';
COMMENT ON COLUMN account.customer_id IS 'Who owns the account';
COMMENT ON COLUMN account.country IS 'Country code (2 letters)';
COMMENT ON COLUMN account.created_at IS 'When the account was opened';

COMMENT ON TABLE balance IS 'Money held on an account, one row per currency';
COMMENT ON COLUMN balance.balance_id IS 'Unique balance id';
COMMENT ON COLUMN balance.account_id IS 'Which account';
COMMENT ON COLUMN balance.currency IS 'EUR / SEK / GBP / USD';
COMMENT ON COLUMN balance.available_amount IS 'How much is left';
COMMENT ON COLUMN balance.updated_at IS 'Last change time';

COMMENT ON TABLE account_transaction IS 'Money moves (IN / OUT) for an account';
COMMENT ON COLUMN account_transaction.transaction_id IS 'Unique transaction id';
COMMENT ON COLUMN account_transaction.account_id IS 'Which account';
COMMENT ON COLUMN account_transaction.amount IS 'How much moved';
COMMENT ON COLUMN account_transaction.currency IS 'Currency of the move';
COMMENT ON COLUMN account_transaction.direction IS 'IN or OUT';
COMMENT ON COLUMN account_transaction.description IS 'Short note';
COMMENT ON COLUMN account_transaction.balance_after_transaction IS 'Balance after this move';
COMMENT ON COLUMN account_transaction.created_at IS 'When it was booked';

COMMENT ON COLUMN account_transaction.ledger_seq IS 'Order number for history (newest last)';
