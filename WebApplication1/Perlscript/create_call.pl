use DBI;
sub get_user_and_phone {
	$dbh = DBI->connect("DBI:mysql:securitydb:localhost", "root", "V8nilla");
	$sql = "SELECT username, phone FROM user where username like (select user from session where status = 'new') LIMIT 1";
	print "$sql\n";
	my $sth = $dbh->prepare($sql);          # prepare the query
	$sth->execute();                        # execute the query

	my @row = $sth->fetchrow_array;
	return ($row[0], $row[1]);
}

sub get_otp {
	my ($user) = @_;
	$dbh = DBI->connect("DBI:mysql:securitydb:localhost", "root", "V8nilla");
	$sql = "SELECT otp FROM session where user = '$user' and  status = 'new' LIMIT 1";
	print "$sql\n";
	my $sth = $dbh->prepare($sql);          # prepare the query
	$sth->execute();                        # execute the query

	my @row = $sth->fetchrow_array;
	return $row[0];
}

sub set_otp_status {
	my ($user) = @_;
	$dbh = DBI->connect("DBI:mysql:securitydb:localhost", "root", "V8nilla");
	$sql = "UPDATE session set status = 'processed' where user = '$user' and  status = 'new' LIMIT 1";
	print "$sql\n";
	$dbh->do($sql);

}

#main
($username, $phone) = get_user_and_phone();
$otp = get_otp($username);
print "$username : $phone : $otp\n";
set_otp_status($username);
