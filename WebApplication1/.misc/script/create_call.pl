#  Creates Call file for Asterisk soft PBX to callout One time password to a designated phone
#  THe inputs namely phone number and OTP is picked from mysql database.
#  THe output call file is copied over to asterisk server for actual call to take place.

#!/usr/bin/perl
use DBI;

sub get_user_and_phone {
	$dbh = DBI->connect("DBI:mysql:securitydb:localhost", "root", "V8nilla");
	$sql = "SELECT username, phone FROM user where username like (select user from session where status = 'new') LIMIT 1";
#	print "$sql\n";
	my $sth = $dbh->prepare($sql);          # prepare the query
	$sth->execute();                        # execute the query

	my @row = $sth->fetchrow_array;
	return ($row[0], $row[1]);
}

sub get_otp {
	my ($user) = @_;
	$dbh = DBI->connect("DBI:mysql:securitydb:localhost", "root", "V8nilla");
	$sql = "SELECT otp FROM session where user = '$user' and  status = 'new' LIMIT 1";
#	print "$sql\n";
	my $sth = $dbh->prepare($sql);          # prepare the query
	$sth->execute();                        # execute the query

	my @row = $sth->fetchrow_array;
	return $row[0];
}

sub create_call_file {
#Channel: SIP/voipbuster/353860774026
#Application: SayDigits
#Data: 4567
	($user, $phone, $otp) = @_;

	$channel = "Channel: SIP/voipbuster/00" . $phone;
	$application = "Application: SayDigits";
	$data = "Data: " .  $otp;

	$fname = "cf/cf_".$user . "_" .$phone . "_" . $otp . ".call";
	open(my $fh, '>', $fname);
	print $fh "$channel\n";
	print $fh "$application\n";
	print $fh "$data\n";
	close $fh;
	
	$cmd = "scp " . $fname . " " . $ASTERISK_UNAME . "@" . $ASTERISK_HOST . ":" . $ASTERISK_HOME . "/" . $fname;
	`$cmd`;
	print "\n$cmd\n\n";
	$cmd = "ssh " . $ASTERISK_UNAME . "@" . $ASTERISK_HOST . " sudo mv " . $ASTERISK_HOME . "/" . $fname . " " . $ASTERISK_DEST_FOLDER;
	print "$cmd\n";
	`$cmd`;
}




sub set_otp_status {
	my ($user) = @_;
	$dbh = DBI->connect("DBI:mysql:securitydb:localhost", "root", "V8nilla");
	$sql = "UPDATE session set status = 'processed' where user = '$user' and  status = 'new' LIMIT 1";
#	print "$sql\n";
	$dbh->do($sql);

}

#main
$ASTERISK_UNAME = "boss";
$ASTERISK_HOST = "192.168.0.150";
$ASTERISK_HOME = "/home/boss";
$ASTERISK_DEST_FOLDER = "/var/spool/asterisk/outgoing";



($username, $phone) = get_user_and_phone();
$otp = get_otp($username);
print "$username : $phone : $otp\n";
create_call_file($username, $phone, $otp);
set_otp_status($username);
