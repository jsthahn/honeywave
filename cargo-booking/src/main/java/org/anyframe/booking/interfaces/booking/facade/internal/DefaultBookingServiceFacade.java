package org.anyframe.booking.interfaces.booking.facade.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.anyframe.booking.application.BookingService;
import org.anyframe.booking.domain.model.booking.Booking;
import org.anyframe.booking.domain.model.booking.BookingId;
import org.anyframe.booking.domain.model.booking.BookingRepository;
import org.anyframe.booking.domain.model.booking.BookingStatus;
import org.anyframe.booking.domain.model.location.LocationRepository;
import org.anyframe.booking.infrastructure.tracking.CargoRoute;
import org.anyframe.booking.infrastructure.tracking.CargoTrackerService;
import org.anyframe.booking.interfaces.booking.facade.BookingServiceFacade;
import org.anyframe.booking.interfaces.booking.facade.dto.BookingDetailDto;
import org.anyframe.booking.interfaces.booking.facade.dto.BookingDto;
import org.anyframe.booking.interfaces.booking.facade.internal.assembler.BookingDetailDtoAssembler;
import org.anyframe.booking.interfaces.booking.facade.internal.assembler.BookingDtoAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DefaultBookingServiceFacade implements BookingServiceFacade,Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
    private BookingRepository bookingRepository;

	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private CargoTrackerService cargoTrackerService;
	
	@Override
	public List<BookingDto> listUserBookings(String userId) {
		List<Booking> bookings = bookingRepository.findByUserId(userId); 
		List<BookingDto> bookingList = new ArrayList<>(bookings.size());
		
		BookingDtoAssembler assembler = new BookingDtoAssembler();
		for (Booking booking : bookings) {
			bookingList.add(assembler.toDto(booking));
		}
		return bookingList;
	}

	@Override
	public BookingDetailDto getBookingDetail(String bookingId) {
		
		Booking booking = bookingRepository.findByBookingId(new BookingId(bookingId));

		String trackingId = booking.getTrackingId();		
		
		BookingDetailDto bookingDetailDto = null;
		
		if (BookingStatus.ACCEPTED.equals(booking.getBookingStatus())) {
			CargoRoute cargoRoute = cargoTrackerService.getCargo(trackingId);
			bookingDetailDto = new BookingDetailDtoAssembler().toDto(booking, cargoRoute);
		}
		else {
			bookingDetailDto = new BookingDetailDtoAssembler().toDto(booking);
		}
		
		return bookingDetailDto;
	}

	@Override
	public List<BookingDto> listNotAcceptedBookings() {
		List<Booking> bookings = bookingRepository.findByBookingStatus(BookingStatus.NOT_ACCEPTED); 
		List<BookingDto> bookingList = new ArrayList<>(bookings.size());
		
		BookingDtoAssembler assembler = new BookingDtoAssembler();
		for (Booking booking : bookings) {
			bookingList.add(assembler.toDto(booking));
		}
		return bookingList;
	}

	@Override
	public String registerBooking(Map<String,String> bookingInformation) {
		Booking booking = bookingService.registerBooking(bookingInformation, BookingStatus.NOT_ACCEPTED);
		
		return booking.getBookingId().getIdString();
	}

	@Override
	public Booking registerAcceptedBooking(Map<String,String> bookingInformation) {
		Booking booking = bookingService.registerBooking(bookingInformation, BookingStatus.ACCEPTED);
		
		return booking;	
	}

	@Override
	public void acceptBooking(String bookingId) {
		bookingService.acceptBooking(new BookingId(bookingId));
	}


	@Override
	public void changeDestination(String bookingId, String destination) {
		bookingService.changeDestination(new BookingId(bookingId), destination);
	}


}
