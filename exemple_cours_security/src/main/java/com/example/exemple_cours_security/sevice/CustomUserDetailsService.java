package com.example.exemple_cours_security.sevice;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.exemple_cours_security.model.User;
import com.example.exemple_cours_security.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
		
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(), 
				user.getPassword(), 
				user.isEnabled(),
				true, true, true,
				user.getRoles()
					.stream()
					.map(r -> new SimpleGrantedAuthority("ROLE_" + r.getName()))
					.collect(Collectors.toList())
				);
	}

}
