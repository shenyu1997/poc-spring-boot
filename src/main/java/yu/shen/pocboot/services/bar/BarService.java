package yu.shen.pocboot.services.bar;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class BarService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BarRemoteClient barRemoteClient;

    public BarRemoteClient getBarRemoteClient() {
        return barRemoteClient;
    }

    public void setBarRemoteClient(BarRemoteClient barRemoteClient) {
        this.barRemoteClient = barRemoteClient;
    }

    public Slice<BarListedDTO> findAll() {
        return barRemoteClient.findAll().map(foo -> modelMapper.map(foo, BarListedDTO.class));
    }
}
